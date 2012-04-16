var informations = new Array();
//informations['5#5#5'] = 'this is an information for tile 5:5';


function displayAdditionalInformation(vis){
                //alert('dAL: clicked at col: '+vis.antsMap.mouseCol+' row:'+vis.antsMap.mouseRow+' turn:'+vis.antsMap.turn);
                var arrPos = (parseInt(vis.antsMap.turn) + 1)+'#'+vis.antsMap.mouseRow+'#'+vis.antsMap.mouseCol;
                if(informations[arrPos] != null){
                    ShowInfoPopup('Info for: '+arrPos,informations[arrPos]);
                    
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
    //alert('LiveData loaded. Count: '+i);
  $('<ul/>', {
    'class': 'my-new-list',
    html: items.join('')
  }).appendTo('body');
  
});

