/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function samdoc_uploadlistener(compId, resultado){
    var hiden = document.getElementById(compId+':respForm:resp');
    hiden.value = resultado.toString();
    var boton = document.getElementById(compId+':respForm:postButton');
    boton.click();
}

window.addEventListener(
  "message",
  function (event) {
      var datos = event.data;
      
      //alert(datos.eventId);
      console.dir(datos);
      
      if(datos.eventId==='expedienteSubido'){
          samdoc_uploadlistener(datos.data.compId, datos.data.resultado);
      }
      
  },
  false);
