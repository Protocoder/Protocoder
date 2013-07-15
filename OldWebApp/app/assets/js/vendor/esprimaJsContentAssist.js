/*******************************************************************************
 * @license
 * Copyright (c) 2012 Contributors
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     Andy Clement (vmware) - initial API and implementation
 *     Andrew Eisenberg (vmware) - implemented visitor pattern
 *******************************************************************************/

/*global define require eclipse esprima window console inTest localStorage*/
window.esprimaJsContentAssist = (function() {

	/**
	 * A prototype of that contains the common built-in types
	 * Types that begin with '?' are functions.  The values after the ':' are the 
	 * argument names.
	 */
	var Types = function() {
		/**
		 * Properties common to all objects - ECMA 262, section 15.2.4.
		 */
		this.Object = {
			// Can't use the real propoerty name here because would override the real methods of that name
			$_$prototype : "Object",
			$_$toString: "?String:",
			$_$toLocaleString : "?String:",
			$_$valueOf: "?Object:",
			$_$hasOwnProperty: "?boolean:property",
			$_$isPrototypeOf: "?boolean:object",
			$_$propertyIsEnumerable: "?boolean:property"
		};
		
		// the global object
		this.Global = {
			// the global 'this'
			"this": "Global",  
			Math: "Math",
			JSON: "JSON",
			Date: "?Date:",
			$$proto : "Object"
		};
		
		/**
		 * Properties common to all Strings - ECMA 262, section 15.5.4
		 */
		this.String = {
			charAt : "?String:index",
			charCodeAt : "?Number:index",
			concat : "?String:array",
			indexOf : "?Number:searchString",
			lastIndexOf : "?Number:searchString",
			length : "?Number:",
			localeCompare : "?Number:Object",
			match : "?Boolean:regexp",
			replace : "?String:searchValue,replaceValue",
			search : "?String:regexp",
			slice : "?String:start,end",
			split : "?Array:separator,[limit]",  // Array of string
			substring : "?String:start,end",
			toLocaleUpperCase : "?String:",
			toLowerCase : "?String:",
			toUpperCase : "?String:",
			trim : "?String:",

			$$proto : "Object"
		};
		
		/**
		 * Properties common to all arrays.  may be incomplete
		 */
		this.Array = {
			length : "Number",
			sort : "?Array:[sorter]",
			concat : "?Array:left,right",
			slice : "?Array:start,end",
			push : "?Object:val",
			$$proto : "Object"
		};
		
		/**
		 * Properties common to all dates.  may be incomplete
		 */
		this.Date = {
			getDay : "?Number:",
			getFullYear : "?Number:",
			getHours : "?Number:",
			getMinutes : "?Number:",
			setDay : "?Number:dayOfWeek",
			setFullYear : "?Number:year",
			setHours : "?Number:hour",
			setMinutes : "?Number:minute",
			setTime : "?Number:millis",
			$$proto : "Object"
		};
		
		this.Boolean = {
			$$proto : "Object"
		};
		
		this.Number = {
			toExponential : "?Number:digits",
			toFixed : "?Number:digits",
			toPrecision : "?Number:digits",
			// do we want to include NaN, MAX_VALUE, etc?	
		
			$$proto : "Object"
		};
		
		this.Function = {
			apply : "?Object:func,[args]",
			"arguments" : "Arguments",
			bind : "?Object:",
			call : "?Object:func,args",
			caller : "Function",
			length : "Number",
			name : "String",
			$$proto : "Object"
		};

		this.Arguments = {
			callee : "Function",
			length : "Number",
			
			$$proto : "Object"
		};

		this.RegExp = {
			g : "Object",
			i : "Object",
			gi : "Object",
			m : "Object",
			exec : "?Array:str",
			test : "?Array:str",
			
			$$proto : "Object"
		};
		
		this.Error = {
			name : "String",
			message : "String",
			stack : "String",
			$$proto : "Object"
		};
		
		
		this.Math = {
		
			// properties
			E : "Number",
			LN2 : "Number",
			LN10 : "Number",
			LOG2E : "Number",
			LOG10E : "Number",
			PI : "Number",
			SQRT1_2 : "Number",
			SQRT2 : "Number",
		
			// Methods
			abs : "?Number:val",
			acos : "?Number:val",
			asin : "?Number:val",
			atan : "?Number:val",
			atan2 : "?Number:val1,val2",
			ceil : "?Number:val",
			cos : "?Number:val",
			exp : "?Number:val",
			floor : "?Number:val",
			log : "?Number:val",
			max : "?Number:val1,val2",
			min : "?Number:val1,val2",
			pow : "?Number:x,y",
			random : "?Number:",
			round : "?Number:val",
			sin : "?Number:val",
			sqrt : "?Number:val",
			tan : "?Number:val",
			$$proto : "Object"
		};

		this.JSON = {
			parse : "?Object:str",
			stringify : "?String:obj",
			$$proto : "Object"
		};

	};

	/**
	 * Generic AST visitor.  Visits all children in source order, if they have a range property.  Children with
	 * no range property are visited first.
	 * 
	 * @param node The AST node to visit
	 * @param context any extra data (is this strictly necessary, or should it be folded into the operation?).
	 * @param operation function(node, context, [isInitialOp]) an operation on the AST node and the data.  Return falsy if
	 * the visit should no longer continue. Return truthy to continue.
	 * @param [postoperation] (optional) function(node, context) an operation that is exectuted after visiting the current node's children.
	 * will only be invoked if operation returns true for the current node
	 */
	function visit(node, context, operation, postoperation) {
		var i, key, child, children;
		
		// uncomment to test that stack heights are consistent
//		var cnt;
//		if (context && context._scopeStack) { 
//			cnt = context._scopeStack.length;
//		}
		if (operation(node, context, true)) {
			// gather children to visit
			children = [];
			for (key in node) {
				if (key !== "range" && key !== "errors" && key !== "target") {
					child = node[key];
					if (child instanceof Array) {
						for (i = 0; i < child.length; i++) {
							if (child[i] && child[i].hasOwnProperty("type")) {
								children.push(child[i]);
							} else if (key === "properties") {
								// might be key-value pair of an object expression
								// don't visit the key since it doesn't have an sloc
								// and it is handle later by inferencing

								// FIXADE - I don't know if this is still necessary since it looks like esprima has changed the
								// way it handles properties in object expressions and they may now be proper AST nodes
								if (child[i].hasOwnProperty("key") && child[i].hasOwnProperty("value")) {
									children.push(child[i].key);
									children.push(child[i].value);
								}
							}
						}
					} else {
						if (child && child.hasOwnProperty("type")) {
							children.push(child);
						}
					}
				}
			}
			
			if (children.length > 0) {
				// sort children by source location
				children.sort(function(left, right) {
					if (left.range && right.range) {
						return left.range[0] - right.range[0];	
					} else if (left.range) {
						return 1;
					} else if (right.range) {
						return -1;
					} else {
						return 0;
					}
				});
				
				// visit children in order
				for (i = 0; i < children.length; i++) {
					visit(children[i], context, operation, postoperation);
				}
			}
			if (postoperation) {
				postoperation(node, context, false);
			}

			// uncomment to test that stack heights are consistent before and after visit
//			if (context && context._scopeStack) { 
//				if (cnt !== context._scopeStack.length) {
//					console.error("Uh oh");
//					console.error(node);
//					console.error(context._scopeStack);
//				}
//			}
		}
	}

	/**
	 * finds the right-most segment of a dotted MemberExpression
	 * if it is an identifier, or null otherwise
	 */
	function findRightMost(node) {
		if (!node) {
			return null;
		}
		if (node.type === "Identifier") {
			return node;
		} else if (node.type === "MemberExpression") {
			return findRightMost(node.property);
		} else {
			return null;
		}
	}
	
	/**
	 * Convert an array of parameters into a string and also compute linked editing positions
	 * @param name name of the function
	 * @param type the type of the function using the following structure '?Type:arg1,arg2,...'
	 * @param offset offset
	 * @return { completion, positions }
	 */
	function calculateFunctionProposal(name, type, offset) {
		var paramsOffset = type.lastIndexOf(":"), paramsStr, params;
		paramsStr = paramsOffset > 0 ? type.substring(paramsOffset+1) : "";
		params = paramsStr.split(",");
		if (!params || params.length === 0) {
			return {completion: name + "()", positions:[]};
		}
		var positions = [];
		var completion = name + '(';
		var plen = params.length;
		for (var p = 0; p < plen; p++) {
			if (p > 0) {
				completion += ', ';
			}
			var argName;
			if (typeof params[p] === "string") {
				// need this because jslintworker.js augments the String prototype with a name() function
				// don't want confusion
				argName = params[p];
			} else if (params[p].name) {
				argName = params[p].name();
			} else {
				argName = params[p];
			}
			positions.push({offset:offset+completion.length+1, length: argName.length});
			completion += argName;
		}
		completion += ')';
		return {completion: completion, positions: positions};
	}
	
	/**
	 * checks that offset overlaps with the given range
	 * Since esprima ranges are zero-based, inclusive of 
	 * the first char and exclusive of the last char, must
	 * use a +1 at the end.
	 * eg- (^ is the line start)
	 *       ^x    ---> range[0,0]
	 *       ^  xx ---> range[2,3]
	 */
	function inRange(offset, range) {
		return range[0] <= offset && range[1]+1 >= offset;
	}
	/**
	 * checks that offset is before the range
	 */
	function isBefore(offset, range) {
		if (!range) {
			return true;
		}
		return offset < range[0];
	}
	
	/**
	 * Determines if the offset is inside this member expression, but after the '.' and before the 
	 * start of the property.
	 * eg, the following returns true:
	 *   foo   .^bar	 
	 *   foo   .  ^ bar
	 * The following returns false:
	 *   foo   ^.  bar
	 *   foo   .  b^ar
	 */
	function afterDot(offset, memberExpr, contents) {
		// check for broken AST
		var end;
		if (memberExpr.property) {
			end = memberExpr.property.range[0];
		} else {
			// no property expression, use the end of the memberExpr as the end to look at
			// in this case assume that the member expression ends just after the dot
			// this allows content assist invocations to work on the member expression when there
			// is no property
			end = memberExpr.range[1] + 2;
		}
		// we are not considered "afeter" the dot if the offset
		// overlaps with the property expression or if the offset is 
		// after the end of the member expression
		if (!inRange(offset, memberExpr.range) ||
			inRange(offset, memberExpr.object.range) ||
			offset > end) {
			return false;
		}
		
		var dotLoc = memberExpr.object.range[1];
		while (contents.charAt(dotLoc) !== "." && dotLoc < end) {
			dotLoc++;
		}
		
		if (contents.charAt(dotLoc) !== ".") {
			return false;
		}
		
		return dotLoc < offset;
	}
	
	/**
	 * @return "top" if we are at a start of a new expression fragment (eg- at an empty line, 
	 * or a new parameter).  "member" if we are after a dot in a member expression.  false otherwise
	 */
	function shouldVisit(root, offset, prefix, contents) {
		/**
		 * A visitor that finds the parent stack at the given location
		 * @param node the AST node being visited
		 * @param parents stack of parent nodes for the current node
		 * @param isInitialVisit true iff this is the first visit of the node, false if this is
		 *   the end visit of the node
		 */ 
		var findParent = function(node, parents, isInitialVisit) {
			if (!isInitialVisit) {
			
				// if we have reached the end of an inRange block expression then 
				// this means we are completing on an empty expression
				if (node.type === "Program" || (node.type === "BlockStatement") &&
						inRange(offset, node.range)) {
					throw "done";
				}
			
				parents.pop();
				// return value is ignored
				return false;
			}
			
			// the program node is always in range even if the range numbers do not line up
			if ((node.range && inRange(offset, node.range)) || node.type === "Program") {
				if (node.type === "Identifier") {
					throw "done";
				}
				parents.push(node);
				if ((node.type === "FunctionDeclaration" || node.type === "FunctionExpression") && 
						node.nody && isBefore(offset, node.body.range)) {
					// completion occurs on the word "function"
					throw "done";
				}
				// special case where we are completing immediately after a '.' 
				if (node.type === "MemberExpression" && !node.property && afterDot(offset, node, contents)) {
					throw "done";
				}
				return true;
			} else {
				return false;
			}
		};
		var parents = [];
		try {
			visit(root, parents, findParent, findParent);
		} catch (done) {
			if (done !== "done") {
				// a real error
				throw(done);
			}
		}

		if (parents && parents.length) {
			var parent = parents.pop();
			if (parent.type === "MemberExpression") {
				if (parent.property && inRange(offset, parent.property.range)) {
					// on the right hand side of a property, eg: foo.b^
					return "member";
				} else if (inRange(offset, parent.range) && afterDot(offset, parent, contents)) {
					// on the right hand side of a dot with no text after, eg: foo.^
					return "member";
				}
			} else if (parent.type === "Program" || parent.type === "BlockStatement") {
				// completion at a new expression
				if (!prefix) {
				}
			} else if (parent.type === "VariableDeclarator" && (!parent.init || isBefore(offset, parent.init.range))) {
				// the name of a variable declaration
				return false;
			} else if ((parent.type === "FunctionDeclaration" || parent.type === "FunctionExpression") && 
					isBefore(offset, parent.body.range)) {
				// a function declaration
				return false;
			}
		}
		return "top";
	}	
	
	/**
	 * finds the final return statement of a function declaration
	 * @param node an ast statement node
	 * @return the lexically last ReturnStatment AST node if there is one, else
	 * null if there is no return statement
	 */
	function findReturn(node) {
		if (!node) {
			return null;
		}
		var type = node.type, maybe, i, last;
		// since we are finding the last return statement, start from the end
		if (type === "BlockStatement") {
			if (node.body && node.body.length > 0) {
				last = node.body[node.body.length-1];
				if (last.type === "ReturnStatement") {
					return last;
				} else {
					return findReturn(last);
				}
			} else {
				return null;
			}
		} else if(type === "WhileStatement" || 
			type === "DoWhileStatement" ||
			type === "ForStatement" ||
			type === "ForInStatement" ||
			type === "CatchClause") {
			
			return findReturn(node.body);
		} else if (type === "IfStatement") {
			maybe = findReturn(node.alternate);
			if (!maybe) {
				maybe = findReturn(node.consequent);
			}
			return maybe;
		} else if (type === "TryStatement") {
			maybe = findReturn(node.finalizer);
			var handlers = node.handlers;
			if (!maybe && handlers) {
				// start from the last handler
				for (i = handlers.length-1; i >= 0; i--) {
					maybe = findReturn(handlers[i]);
					if (maybe) {
						break;
					}
				}
			}
			if (!maybe) {
				maybe = findReturn(node.block);
			}
			return maybe;
		} else if (type === "SwitchStatement") {
			var cases = node.cases;
			if (cases) {
				// start from the last handler
				for (i = cases.length-1; i >= 0; i--) {
					maybe = findReturn(cases[i]);
					if (maybe) {
						break;
					}
				}
			}
			return maybe;
		} else if (type === "SwitchCase") {
			if (node.consequent && node.consequent.length > 0) {
				last = node.consequent[node.consequent.length-1];
				if (last.type === "ReturnStatement") {
					return last;
				} else {
					return findReturn(last);
				}
			} else {
				return null;
			}
			
		} else if (type === "ReturnStatement") {
			return node;
		} else {
			// don't visit nested functions
			// expression statements, variable declarations,
			// or any other kind of node
			return null;
		} 
	}
	
	/**
	 * updates a function type to include a new return type.
	 * function types are specified like this: ?returnType:[arg-n...]
	 * return type is the name of the return type, arg-n is the name of
	 * the nth argument.
	 */
	function updateReturnType(originalFunctionType, newReturnType) {
		if (! originalFunctionType || originalFunctionType.charAt(0) !== "?") {
			// not a valid function type
			return newReturnType;
		}
		
		var end = originalFunctionType.lastIndexOf(":");
		if (!end) {
			// not a valid function type
			return newReturnType;
		}
		return "?" + newReturnType + originalFunctionType.substring(end);
	}
	/**
	 * checks to see if this file looks like an AMD module
	 * @return true iff there is a top-level call to 'define'
	 */
	function checkForAMD(node) {
		var body = node.body;
		// FIXADE should we only be handling the case where there is more than one module?
		if (body && body.length === 1) {
			if (body[0].type === "ExpressionStatement" && 
				body[0].expression.type === "CallExpression" && 
				body[0].expression.callee.name === "define") {
				
				// found it.
				return body[0].expression;
			}
		}
		return null;
	}
	
	/**
	 * if the type passed in is a function type, extracts the return type
	 * otherwise returns as is
	 */
	function extractReturnType(fnType) {
		if (fnType.charAt(0) === '?') {
			var typeEnd = fnType.lastIndexOf(':');
			typeEnd = typeEnd >0 ? typeEnd : fnType.length;
			fnType = fnType.substring(1,typeEnd);
		}
		return fnType;	
	}
	
	/**
	 * checks to see if this function is a module definition
	 * and if so returns an array of module definitions
	 * 
	 * if this is not a module definition, then just return an array of Object for each type
	 */
	function findModuleDefinitions(fnode, env) {
		var paramTypes = [], params = fnode.params, i;
		if (params.length > 0) {
			if (env.indexer && env.amdModule) {
				var args = env.amdModule.arguments;
				// the function definition must be the last argument of the call to define 
				if (args.length > 1 && args[args.length-1] === fnode) {
					// the module names could be the first or second argument
					var moduleNames = null;
					if (args.length === 3 && args[0].type === "Literal" && args[1].type === "ArrayExpression") {
						moduleNames = args[1].elements;
					} else if (args.length === 2 && args[0].type === "ArrayExpression") {
						moduleNames = args[0].elements;
					}
					if (moduleNames) {
						for (i = 0; i < params.length; i++) {
							if (i < moduleNames.length && moduleNames[i].type === "Literal") {
								// resolve the module name from the indexer
								var summary = env.indexer.retrieveSummary(moduleNames[i].value);
								if (summary) {
									var typeName;
									if (typeof summary.provided === "string") {
										// module provides a builtin type, just remember that type
										typeName = summary.provided;
									} else {
										// module provides a composite type
										// must create a type to add the summary to
										typeName = env.newScope();
										env.popScope();
										env.mergeSummary(summary, typeName);
									}
									paramTypes.push(typeName);
								} else {
									paramTypes.push("Object");
								}
							} else {
								paramTypes.push("Object");
							}
						}
					}
				}
			}
			
			
			if (params.length === 0) {
				for (i = 0; i < params.length; i++) {
					paramTypes.push("Object");
				}
			}
		}
		return paramTypes;
	}

	/**
	 * This function takes the current AST node and does the first inferencing step for it.
	 * @param node the AST node to visit
	 * @param env the context for the visitor.  See computeProposals below for full description of contents
	 */
	function proposalGenerator(node, env) {
		var type = node.type, oftype, name, i, property, params, newTypeName;
		
		// FIXADE Do we still want to do this?
		if (type === "VariableDeclaration" && isBefore(env.offset, node.range)) {
			// must do this check since "VariableDeclarator"s do not have their range set correctly in the version of esprima being used now
			return false;
		}
		
		if (type === "Program") {
			// check for potential AMD module.  Can add other module kinds later
			env.amdModule = checkForAMD(node);
		} else if (type === "BlockStatement") {
			node.inferredType = env.newScope();
		} else if (type === "Literal") {
			oftype = (typeof node.value);
			node.inferredType = oftype[0].toUpperCase() + oftype.substring(1, oftype.length);
		} else if (type === "ArrayExpression") {
			node.inferredType = "Array";
		} else if (type === "ObjectExpression") {
			// for object literals, create a new object type so that we can stuff new properties into it.
			// we might be able to do better by walking into the object and inferring each RHS of a 
			// key-value pair
			newTypeName = env.newObject();
			node.inferredType = newTypeName;
			for (i = 0; i < node.properties.length; i++) {
				property = node.properties[i];
				// only remember if the property is an identifier
				if (property.key && property.key.name) {
					// first just add as an object property.
					// after finishing the ObjectExpression, go and update 
					// all of the variables to reflect their final inferred type
					env.addVariable(property.key.name, node, "Object");
					if (property.value.type === "FunctionExpression") {
						// RHS is a function, remember the name in case it is a constructor
						property.value.fname = property.key.name;
					}
				}
			}
			
		} else if (type === "FunctionDeclaration" || type === "FunctionExpression") {

			if (node.id) {
				// true for function declarations
				name = node.id.name;
			} else if (node.fname) {
				// true for rhs of assignment to function expression
				name = node.fname;
			}
			params = [];
			if (node.params) {
				for (i = 0; i < node.params.length; i++) {
					params[i] = node.params[i].name;
				}
			}
			
			// assume that function name that starts with capital is 
			// a constructor
			if (name && node.body && name.charAt(0) === name.charAt(0).toUpperCase()) {
				// create new object so that there is a custom "this"
				node.body.isConstructor = true;
				newTypeName = env.newObject(name);
			} else {
				// temporarily use "Object" as type, but this may change once we 
				// walk through to get to a return statement
				newTypeName = "Object";
			}
			newTypeName = "?" + newTypeName + ":" + params;
			node.inferredType = newTypeName;
			
			if (name && !isBefore(env.offset, node.range)) {
				// if we have a name, then add it to the scope
				env.addVariable(name, node.target, newTypeName);
			}
			
			// now add the scope for inside the function
			env.newScope();
			env.addVariable("arguments", node.target, "Arguments");

			// add parameters to the current scope
			if (params.length > 0) {
				var moduleDefs = findModuleDefinitions(node, env);
				for (i = 0; i < params.length; i++) {
					env.addVariable(params[i], node.target, moduleDefs[i]);
				}	
			}
		} else if (type === "VariableDeclarator") {
			if (node.id.name && node.init && node.init.type === "FunctionExpression") {
				// RHS is a function, remember the name in case it is a constructor
				node.init.fname = node.id.name;
			}
		} else if (type === "AssignmentExpression") {
			if (node.left.type === "Identifier" && node.right.type === "FunctionExpression") {
				// RHS is a function, remember the name in case it is a constructor
				node.right.fname = node.left.name;
			}
		} else if (type === "CatchClause") {
			// create a new scope for the catch parameter
			node.inferredType = env.newScope();
			if (node.param) {	
				node.param.inferredType = "Error";
				env.addVariable(node.param.name, node.target, "Error");
			}
		} else if (type === "MemberExpression") {
			if (node.property) {
				// keep track of the target of the property expression
				// so that its type can be used as the seed for finding properties
				node.property.target = node.object;
			}
		}
		return true;
	}
	
	/**
	 * called as the post operation for the proposalGenerator visitor.
	 * Finishes off the inferencing and adds all proposals
	 */
	function proposalGeneratorPostOp(node, env) {
		var type = node.type, name, inferredType, newTypeName, rightMost, kvps, i;
		
		if (type === "Program") {
			// if we've gotten here and we are still in range, then 
			// we are completing as a top-level entity with no prefix
			env.createProposals();
		} else if (type === "BlockStatement" || type === "CatchClause") {
			if (inRange(env.offset, node.range)) {
				// if we've gotten here and we are still in range, then 
				// we are completing as a top-level entity with no prefix
				env.createProposals();
			}
		
			env.popScope();
			
		} if (type === "MemberExpression") {
			if (afterDot(env.offset, node, env.contents)) {
				// completion after a dot with no prefix
				env.createProposals(env.scope(node.object));
			}
			// inferred type is the type of the property expression
			// node.propery will be null for mal-formed asts
			node.inferredType = node.property ? node.property.inferredType : node.object.inferredType;
		} else if (type === "CallExpression") {
			// apply the function
			var fnType = node.callee.inferredType;
			fnType = extractReturnType(fnType);
			node.inferredType = fnType;
		} else if (type === "NewExpression") {
			// FIXADE we have a slight problem here.
			// constructors that are called like this: new foo.Bar()  should have an inferred type of foo.Bar,
			// This ensures that another constructor new baz.Bar() doesn't conflict.  However, 
			// we are only taking the final prefix and assuming that it is unique.
			node.inferredType = extractReturnType(node.callee.inferredType);
		} else if (type === "ObjectExpression") {
			// now that we know all the types of the values, use that to populate the types of the keys
			// FIXADE esprima has changed the way it does key-value pairs,  Should do it differently here
			kvps = node.properties;
			for (i = 0; i < kvps.length; i++) {
				if (kvps[i].hasOwnProperty("key")) {
					// only do this for keys that are identifiers
					// set the proper inferred type for the key node
					// and also update the variable
					name = kvps[i].key.name;
					if (name) {
						inferredType = kvps[i].value.inferredType;
						kvps[i].key.inferredType = inferredType;
						env.addVariable(name, node, inferredType);
					}
				}
			}
			env.popScope();
		} else if (type === "BinaryExpression") {
			if (node.operator === "+" || node.operator === "-" || node.operator === "/" || 
					node.operator === "*") {
				// assume number for now
				// rules are really much more complicated
				node.inferredType = "Number";
			} else {
				node.inferredType = "Object";
			}
		} else if (type === "UpdateExpression" || type === "UnaryExpression") {
			// assume number for now.  actual rules are much more complicated
			node.inferredType = "Number";
		} else if (type === "FunctionDeclaration" || type === "FunctionExpression") {
			env.popScope();
			if (node.body) {
				if (node.body.isConstructor) {
					// an extra scope was created for the implicit 'this'
					env.popScope();

					// now add a reference to the constructor
					env.addOrSetVariable(extractReturnType(node.inferredType), node.target, node.inferredType);
				} else {
					// a regular function.  try updating to a more explicit return type
					var returnStatement = findReturn(node.body);
					if (returnStatement) {
						node.inferredType = updateReturnType(node.inferredType, returnStatement.inferredType);
						// if there is a name, then update that as well
						var fname;
						if (node.id) {
							// true for function declarations
							fname = node.id.name;
						} else if (node.fname) {
							// true for rhs of assignment to function expression
							fname = node.fname;
						}
						if (fname) {
							env.addOrSetVariable(fname, node.target, node.inferredType);
						}				
					}
				}
			}
		} else if (type === "VariableDeclarator") {
			if (node.init) {
				inferredType = node.init.inferredType;
			} else {
				inferredType = "Object";
			}
			node.inferredType = inferredType;
			env.addVariable(node.id.name, node.target, inferredType);

		} else if (type === "AssignmentExpression") {
			inferredType = node.right.inferredType;
			node.inferredType = inferredType;
			// when we have this.that.theOther.f need to find the right-most identifier
			rightMost = findRightMost(node.left);
			if (rightMost) {
				env.addOrSetVariable(rightMost.name, rightMost.target, inferredType);
			}
		} else if (type === 'Identifier') {
			if (inRange(env.offset, node.range)) {
				// We're finished compute all the proposals
				env.createProposals(env.scope(node.target));
			}
			
			name = node.name;
			newTypeName = env.lookupName(name, node.target);
			if (newTypeName) {
				// name already exists
				node.inferredType = newTypeName;
			} else {
				// If name doesn't already exist, then just assume "Object".
				node.inferredType = "Object";
			}
		} else if (type === "ThisExpression") {
			node.inferredType = env.lookupName("this");
		} else if (type === "ReturnStatement") {
			if (node.argument) {
				node.inferredType = node.argument.inferredType;
			}
		}
		
		if (!node.inferredType) {
			node.inferredType = "Object";
		}
	}

	function parse(contents) {
		var parsedProgram = esprima.parse(contents, {
			range: true,
			tolerant: true,
			comment: true
		});
		return parsedProgram;
	}
	
	/**
	 * add variable names from inside a jslint global directive
	 */
	function addJSLintGlobals(root, env) {
		if (root.comments) {
			for (var i = 0; i < root.comments.length; i++) {
				if (root.comments[i].type === "Block" && root.comments[i].value.substring(0, "global".length) === "global") {
					var globals = root.comments[i].value;
					var splits = globals.split(/\s+/);
					for (var j = 1; j < splits.length; j++) {
						if (splits[j].length > 0) {
							env.addOrSetVariable(splits[j]);
						}
					}
					break;
				}
			}
		}
	}
	
	/**
	 * Adds global variables defined in dependencies
	 */
	function addIndexedGlobals(env) {
		// no indexer means that we should not consult indexes for extra type information
		if (env.indexer) {
			// get the list of summaries relevant for this file
			// add it to the global scope
			var summaries = env.indexer.retrieveGlobalSummaries();
			for (var fileName in summaries) {
				if (summaries.hasOwnProperty(fileName)) {
					env.mergeSummary(summaries[fileName], "Global");
				}
			}
		} 
	}
	
	function isMember(val, inArray) {
		for (var i = 0; i < inArray.length; i++) {
			if (inArray[i] === val) {
				return true;
			}
		}
		return false;
	}

	/**
	 * the prefix of a completion should not be included in the completion itself
	 * must explicitly remove it
	 */
	function removePrefix(prefix, string) {
		return string.substring(prefix.length);
	}
	
	/** Creates the environment object that stores type information*/
	function createEnvironment(buffer, uid, completionKind, offset, prefix, indexer) {
		if (!offset) {
			offset = buffer.length;
		}
		if (!prefix) {
			prefix = "";
		}
		// prefix for generating local types
		// need to add a unique id for each file so that types defined in dependencies don't clash with types
		// defined locally
		var namePrefix = "gen~" + uid + "~";

		return {
			/** Each element is the type of the current scope, which is a key into the types array */
			_scopeStack : ["Global"],
			/** 
			 * a map of all the types and their properties currently known 
			 * when an indexer exists, local storage will be checked for extra type information
			 */
			_allTypes : new Types(),
			/** if this is an AMD module, then the value of this property is the 'define' call expression */
			amdModule : null,	
			/** a counter used for creating unique names for object literals and scopes */
			_typeCount : 0,
			/** 
			 * "member" or "top" or null
			 * if Member, completion occurs after a dotted member expression.  
			 * if top, completion occurs as the start of a new expression.
			 * if null, then this environment is not for completion, but for building a summary
			 */
			_completionKind : completionKind,
			/** the indexer for thie content assist invocation.  Used to track down dependencies */
			indexer: indexer,
			/** an array of proposals generated */
			proposals : [], 
			/** the offset of content assist invocation */
			offset : offset, 
			/** 
			 * the location of the start of the area that will be replaced 
			 */
			replaceStart : offset - prefix.length, 
			/** the prefix of the invocation */
			prefix : prefix, 
			/** the entire contents being completed on */
			contents : buffer,
			newName: function() {
				return namePrefix + this._typeCount++;
			},
			/** Creates a new empty scope and returns the name of the scope*/
			newScope: function() {
				// the prototype is always the currently top level scope
				var targetType = this.scope();
				var newScopeName = this.newName();
				this._allTypes[newScopeName] = {
					$$proto : targetType
				};
				this._scopeStack.push(newScopeName);
				return newScopeName;
			},
			
			/** Creates a new empty object scope and returns the name of this object */
			newObject: function(newObjectName) {
				// object needs its own scope
				this.newScope();
				// if no name passed in, create a new one
				newObjectName = newObjectName? newObjectName : this.newName();
				// assume that objects have their own "this" object
				// prototype of Object
				this._allTypes[newObjectName] = {
					$$proto : "Object"
				};
				this.addVariable("this", null, newObjectName);
				
				return newObjectName;
			},
			
			/** removes the current scope */
			popScope: function() {
				// Can't delete old scope since it may have been assigned somewhere
				// but must remove "this" when outside of the scope
				this.removeVariable("this");
				var oldScope = this._scopeStack.pop();
				return oldScope;
			},
			
			/**
			 * returns the type for the current scope
			 * if a target is passed in (optional), then use the
			 * inferred type of the target instead (if it exists)
			 */
			scope : function(target) {
				if (target && target.inferredType) {
					// check for function literal
					return target.inferredType.charAt(0) === "?" ? "Function" : target.inferredType;
				} else {
					// grab topmost scope
					return this._scopeStack[this._scopeStack.length -1];
				}
			},
			
			/** adds the name to the target type.
			 * if target is passed in then use the type corresponding to 
			 * the target, otherwise use the current scope
			 */
			addVariable : function(name, target, type) {
				this._allTypes[this.scope(target)][name] = type ? type : "Object";
			},
			
			/** removes the variable from the current type */
			removeVariable : function(name, target) {
				delete this._allTypes[this.scope(target)][name];
			},
			
			/** 
			 * like add variable, but first checks the prototype hierarchy
			 * if exists in prototype hierarchy, then replace the type
			 */
			addOrSetVariable : function(name, target, type) {
				var targetType = this.scope(target);
				var current = this._allTypes[targetType], found = false;
				// if no type provided, assume object
				type = type ? type : "Object";
				while (current) {
					if (current[name]) {
						// found it, just overwrite
						current[name] = type;
						found = true;
						break;
					} else {
						current = current.$$proto;
					}
				}
				if (!found) {
					// not found, so just add to current scope
					this._allTypes[targetType][name] = type;
				}
			},
						
			/** looks up the name in the hierarchy */
			lookupName : function(name, target, applyFunction) {
			
				// translate function names on object into safe names
				var swapper = function(name) {
					switch (name) {
						case "prototype":
						case "toString":
						case "hasOwnProperty":
						case "toLocaleString":
						case "valueOf":
						case "isProtoTypeOf":
						case "propertyIsEnumerable":
							return "$_$" + name;
						default:
							return name;
					}
				};
			
				var innerLookup = function(name, type, allTypes) {
					var res = type[name];
					
					// if we are in Object, then we may have special prefixed names to deal with
					var proto = type.$$proto;
					if (res) {
						return res;
					} else {
						if (proto) {
							return innerLookup(name, allTypes[proto], allTypes);
						}
						return null;
					}
				};
				var targetType = this._allTypes[this.scope(target)];
				var res = innerLookup(swapper(name), targetType, this._allTypes);
				return res;
			},
			
			/**
			 * adds a file summary to this module
			 */
			mergeSummary : function(summary, targetTypeName) {
			
				// add the extra types that don't already exists
				for (var type in summary.types) {
					if (summary.types.hasOwnProperty(type) && !this._allTypes[type]) {
						this._allTypes[type] = summary.types[type];
					}
				}
				
				// now augment the target type with the provided properties
				var targetType = this._allTypes[targetTypeName];
				for (var providedProperty in summary.provided) {
					if (summary.provided.hasOwnProperty(providedProperty)) {
						// the targetType may already have the providedProperty defined
						// but should override
						targetType[providedProperty] = summary.provided[providedProperty];
					}
				}
			},
			
			createProposals : function(targetType) {
				if (!this._completionKind) {
					// not generating proposals.  just walking the file
					return;
				}
			
				if (!targetType) {
					targetType = this.scope();
				}
//				if (targetType.charAt(0) === '?') {
//					targetType = "Function";
//				}
				var prop, propName, propType, proto, res, type = this._allTypes[targetType];
				proto = type.$$proto;
				
				for (prop in type) {
					if (type.hasOwnProperty(prop)) {
						if (prop === "$$proto") {
							continue;
						}
						if (!proto && prop.indexOf("$_$") === 0) {
							// no prototype that means we must decode the property name
							propName = prop.substring(3);
						} else {
							propName = prop;
						}
						if (propName === "this" && this._completionKind === "member") {
							// don't show "this" proposals for non-top-level locations
							// (eg- this.this is wrong)
							continue;
						}
						if (propName.indexOf(this.prefix) === 0) {
							propType = type[prop];
							if (propType.charAt(0) === '?') {
								// we have a function
								res = calculateFunctionProposal(propName, 
										propType, this.replaceStart - 1);
								this.proposals.push({ 
									proposal: removePrefix(this.prefix, res.completion), 
									description: res.completion + " : " + this.createReadableType(propType) + " (esprima)", 
									positions: res.positions, 
									escapePosition: this.replaceStart + res.completion.length 
								});
							} else {
								this.proposals.push({ 
									proposal: removePrefix(this.prefix, propName),
									description: propName + " : " + this.createReadableType(propType) + " (esprima)"
								});
							}
						}
					}
				}
				// walk up the prototype hierarchy
				if (proto) {
					this.createProposals(proto);
				}
				// We're done!
				throw "done";
			},
			
			/**
			 * creates a human readable type name from the name given
			 */
			createReadableType : function(typeName) {
				if (typeName.charAt(0) === "?") {
					// a function, use the return type
					var nameEnd = typeName.indexOf(":");
					if (nameEnd === -1) {
						nameEnd = typeName.length;
					}
					return typeName.substring(1, nameEnd);
				} else if (typeName.indexOf("gen~") === 0) {
					// a generated object
					// create a summary
					var type = this._allTypes[typeName];
					var res = "{ ";
					for (var val in type) {
						if (type.hasOwnProperty(val) && val !== "$$proto") {
							res += val + " ";
						}
					}
					return res + "}";
				} else {
					return typeName;
				}
			}
		};
	}
	
	function getBuiltInTypes(environment) {
		var builtInTypes = [];
		for (var prop in environment._allTypes) {
			if (environment._allTypes.hasOwnProperty(prop)) {
				builtInTypes.push(prop);
			}
		}
		return builtInTypes;
	}
	
	/**
	 * filters types from the environment that should not be exported
	 * FIXADE should also walk through and remove all unreachable types
	 */
	function filterTypes(environment, builtInTypes, kind) {
		if (kind === "global") {
			// for global dependencies must keep the global scope, but remove all builtin global variables
			var global = environment._allTypes.Global;
			delete global["this"];
			delete global.Date;
			delete global.Math;
			delete global.JSON;
			delete global.$$proto;
		} else {
			delete environment._allTypes.Global;
		}
	
		for (var i = 0; i < builtInTypes.length; i++) {
			if (builtInTypes[i] !== "Global") {
				delete environment._allTypes[builtInTypes[i]];
			}
		}
	}

	/**
	 * indexer is optional.  When there is no indexer passed in
	 * the indexes will not be consulted for extra references
	 */
	function EsprimaJavaScriptContentAssistProvider(indexer) {
		this.indexer = indexer;
	}
	
	/**
	 * Main entry point to provider
	 */
	EsprimaJavaScriptContentAssistProvider.prototype = {
	
		_doVisit : function(root, environment) {
			// first augment the global scope with things we know
			addJSLintGlobals(root, environment);
			addIndexedGlobals(environment);
			try {
				visit(root, environment, proposalGenerator, proposalGeneratorPostOp);
			} catch (done) {
				if (done !== "done") {
					// a real error
					throw done;
				}
			}
		},
		
		/**
		 * implements the Orion content assist API
		 */
		computeProposals: function(buffer, offset, context) {
			try {
				var root = parse(buffer);
				// note that if selection has length > 0, then just ignore everything past the start
				var completionKind = shouldVisit(root, offset, context.prefix, buffer);
				if (completionKind) {
					var environment = createEnvironment(buffer, "local", completionKind, offset, context.prefix, this.indexer);
					this._doVisit(root, environment);
					environment.proposals.sort(function(l,r) {
						if (l.description < r.description) {
							return -1;
						} else if (r.description < l.description) {
							return 1;
						} else {
							return 0;
						}
					});
					return environment.proposals;
				} else {
					// invalid completion location
					return {};
				}
			} catch (e) {
				if (console && console.log) {
					console.log(e.message);
					console.log(e.stack);
				}
				throw (e);
			}
		},
		
		/**
		 * Computes a summary of the file that is suitable to be stored locally and used as a dependency 
		 * in another file
		 */
		computeSummary: function(buffer, fileName) {
			try {
				var root = parse(buffer);
				var environment = createEnvironment(buffer, fileName);
				// keep track of built-in types so they can be removed later
				var builtInTypes = getBuiltInTypes(environment);
				
				this._doVisit(root, environment);
				
				var provided;
				var kind;
				if (environment.amdModule) {
					// provide the exports of the AMD module
					// the exports is the return value of the final argument
					var args = environment.amdModule.arguments;
					var modType;
          console.log(args, args.length);
					if (args && args.length > 0) {
						modType = extractReturnType(args[args.length-1].inferredType);
					} else {
						modType = "Object";
					}
					if (isMember(modType, builtInTypes) || modType.charAt(0) === '?') {
						// this module provides a primitive type or a function
						provided = modType;
					} else {
						// this module provides a composite type
						provided = environment._allTypes[modType];
					}
					kind = "AMD";
				} else {
					// if not AMD module, then return everything that is in the global scope
					provided = environment._allTypes.Global;
					kind = "global";
				}

				// now filter the builtins since they are always available
				filterTypes(environment, builtInTypes, kind);
				
				return {
					provided : provided,
					types : environment._allTypes,
					kind : kind
				};
			} catch (e) {
				if (console && console.log) {
					console.log(e.message);
					console.log(e.stack);
				}
				throw (e);
			}
		}
	};
	return EsprimaJavaScriptContentAssistProvider;
})();