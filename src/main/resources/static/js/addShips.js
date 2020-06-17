/*
$(".btnAddShips").click(function(){
  var types=['carrier','battleship','submarine','destroyer','patrol_boat'];
  var ships=[];
  for(let i=0; i<5; i++){
    ships[i]={
      type : types[i],
      locations : getLocations(types[i])
    }
  }
console.log(ships);
});
*/



function getLocations(shipType){
  let x = parseInt($(`#${shipType}`).attr("data-gs-x"))+1;
  let y = parseInt($(`#${shipType}`).attr("data-gs-y"))+1;
  let height = parseInt($(`#${shipType}`).attr("data-gs-height"));
  let width = parseInt($(`#${shipType}`).attr("data-gs-width"));
  let positions=[];
  if(height>width){
    for(let i=y; i< (y+height); i++){
      positions[i]= String.fromCharCode(i+64) + x ;
    }
  }else{
    for(let i=x; i< (x+width); i++){
      positions[i]= String.fromCharCode(y+64) + i ;
    }
  }
  return positions.filter(Boolean);
  // action goes here!!
}

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};





function setBarcos (){

var shipType=['carrier','battleship','submarine','destroyer','patrol_boat'];
  var ships=[];
  for(let i=0; i<5; i++){
    ships[i]={
      shipType : shipType[i],
      shipLocations : getLocations(shipType[i])
    }
  }
console.log(ships);



 $.post({
     url: "/api/games/players/" + getParameterByName('gp') +"/ships",
     data: JSON.stringify(ships),
   dataType: "text",
   contentType: "application/json"
 })
 .done(function () {
   Swal.fire({
                         title: 'ships added !',
                         showCancelButton: false,
                         confirmButtonColor: '#3085d6',
                         cancelButtonColor: '#d33',
                         confirmButtonText: 'OK'
                       }).then((result) => {
                        window.location.href = '/web/game.html?gp='+getParameterByName('gp');

                       })


 })
}