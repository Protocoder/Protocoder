var Protocoder = function () { 
} 

Protocoder.prototype.init = function () { 
	//this is required 
	this.event = new Event();
	this.communication = new Communication(this, true);
	
	//this is optional depending on the editor
    this.communication.listApps("projects");
    this.communication.listApps("examples");
    this.ui = new Ui(this);
    this.editor = new Editor(this);
    this.dashboard = new Dashboard(this); 
    this.reference = new Reference(this);
    this.communication.getReference(this);
}