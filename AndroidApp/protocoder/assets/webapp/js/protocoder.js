var Protocoder = function () { 
	this.init();
} 

Protocoder.prototype.init = function () { 

    this.ui = new Ui();
    this.editor = new Editor();

    this.communication = new Communication(true);
    this.communication.listApps("user");
    this.communication.listApps("example");
    this.dashboard = new Dashboard(); 
    this.reference = new Reference();
    this.communication.getReference();
}