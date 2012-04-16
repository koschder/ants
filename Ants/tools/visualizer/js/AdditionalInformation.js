var informations = new Array();

function displayAdditionalInformation(vis){
    //alert('dAL: clicked at col: '+vis.antsMap.mouseCol+' row:'+vis.antsMap.mouseRow+' turn:'+vis.antsMap.turn);
    var pos = (parseInt(vis.antsMap.turn)+1)+'#'+vis.antsMap.mouseRow+'#'+vis.antsMap.mouseCol;
    //var title = (parseInt(vis.antsMap.turn)+1)+'#'+vis.antsMap.mouseRow+'#'+vis.antsMap.mouseCol;
    if(informations[pos] != null){
        ShowInfoPopup('Info for: '+pos,informations[pos]);
        
    }
}

function ShowInfoPopup(title, text) {
    //$('#dialog').dialog({ modal: true });
    
    var div = $('#dialog').html(text).dialog({
        title: title,
        modal: false
    })
};


$.getJSON($liveInfoFile, function(data) {
    var i = 0;
  $.each(data, function(key, val) {
    if(informations[key])
    {
        informations[key] += "<br/>"+val;
    }else{
        informations[key] = val;
    }
    i++;
  });
});

