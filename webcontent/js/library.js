

function removeDuplicateDialogs(dialogId) {

    dialogId = dialogId.replace(/\:/g, '\\:');

    var dialogs = jQuery("div[id=" + dialogId + "]");
    var numOfDialogs = dialogs.length;
    numOfDialogs = numOfDialogs - 1;
    if (Number(numOfDialogs) > 0) {
	    for (var i = 0; i < numOfDialogs; i++) {
	        jQuery(dialogs[i]).remove();
	    }
    }
}


function showDocumentNoteDialogs(dialogId, dialogVar) {

    dialogId = dialogId.replace(/\:/g, '\\:');

    var dialogs = jQuery("div[id=" + dialogId + "]");
    var numOfDialogs = dialogs.length;
    if (Number(numOfDialogs) > 1) {
	    for (var i = numOfDialogs - 1; i > 0; i--) {
	        jQuery(dialogs[i]).remove();
	    }
    }
    PF(dialogVar).show();
}

function start() {
    PF('downloadStatusDialog').show();
}
 
function stop() {
    PF('downloadStatusDialog').hide();
}