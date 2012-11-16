var informations = new Array();

function displayAdditionalInformation(vis){
	//$('#dialog').hide();
    //alert('dAL: clicked at col: '+vis.antsMap.mouseCol+' row:'+vis.antsMap.mouseRow+' turn:'+vis.antsMap.turn);
    var pos = (parseInt(vis.antsMap.turn)+1)+'#'+vis.antsMap.mouseRow+'#'+vis.antsMap.mouseCol;
    //var title = (parseInt(vis.antsMap.turn)+1)+'#'+vis.antsMap.mouseRow+'#'+vis.antsMap.mouseCol;
    if(informations[pos] != null && informations[pos] != ''){
		
		var val = informations[pos];
	
		var linkedval = "";
		
		while(val.indexOf('r:') >= 0 && val.indexOf('c:') >= 0){
			//<r:20 c:39>
			linkedval += val.substr(0,val.indexOf(';r:')+1);
			val = val.substr(val.indexOf(';r:')+1);
			
			var text = val.substr(0,val.indexOf('>'));
			
			val = val.substr(text.length);
			
			var row = text.substr(2,text.indexOf(' ')-2);
			var col = text.substr(text.indexOf('c')+2);
			showTile(row,col);			
			linkedval += "<a href=\"javascript:showTile("+row+","+col+");\">"+text+"</a>";
		}
		
		
		
		linkedval += val;
		
        ShowInfoPopup('Info for: '+pos,linkedval);
        
    }
}

function ShowInfoPopup(title, text) {
    //$('#dialog').dialog({ modal: true });
    
    var div = $('#dialog').html(text).dialog({
        title: title,
        modal: false,
		position: "left"
    })
};

function showTile(row,col){

//this.hint row 40 | col 39
//var hintPos = "row "+row+" | col "+col; // 
//alert();
//alert(hintPos);
//visualizer.hint = hintPos;
var scale = visualizer.map.scale;
visualizer.map.ctx.fillStyle = "#ee9933";
visualizer.map.ctx.fillRect( col*scale,row*scale, scale, scale);
//visualizer.director.draw();
}


$.getJSON($liveInfoFile, function(data) {
    var i = 0;
  $.each(data, function(key, val) {
    
	if(key.indexOf("#") > 0){
		key = key.substr(key.indexOf("#")+1);
		
		if(informations[key])
		{
			informations[key] = informations[key]+"<br/><br/>"+val;
		}else{
			informations[key] = val;
		}
		i++;
	}
  });
});

