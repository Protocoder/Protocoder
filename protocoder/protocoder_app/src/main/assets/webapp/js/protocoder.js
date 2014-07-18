var Protocoder = function () { 
} 

Protocoder.prototype.init = function () { 

    this.ui = new Ui();
    this.editor = new Editor();
    this.communication = new Communication(true);
    this.communication.listApps("projects");
    this.communication.listApps("examples");
    this.dashboard = new Dashboard(); 
    this.reference = new Reference();
    this.communication.getReference();
}