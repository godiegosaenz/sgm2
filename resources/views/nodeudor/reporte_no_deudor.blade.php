

<!DOCTYPE html>
<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <title></title>
    <style media="screen">
      .div {
        padding-top: 2px;
      }

      .encabezado{
        /*border: 1px solid;*/
        text-align: center;
        font-size: 15px;

      }
      .encabezado1{
        text-align: center;
        padding-top: 5px;
        font-size: 13px;
      }
      .verticalText {
        writing-mode: vertical-lr;
        transform: rotate(270deg);
      }
      table {
        font-size:11.20px
        border-collapse : collapse;
        border-spacing:  0px;

      }
      .hr{
        page-break-after: always;
        border: none;
        margin: 0;
        padding: 0;
      }
    </style>
  
    <style>
			.logo{
				width: 300px;
			}
			p {
				margin-bottom: 0px;
				margin-top: 0px;
			}
      footer { position: fixed; bottom: 30px; left: 0px; right: 0px; background-color: white; height: 60px; margin-right: 80px }

	  </style>

  </head>
  <body>
    
    
    <footer>
      <table class="ltable" width="112.5%" height="100px">
              <td  colspan="6"  height="60px" style="border:0; background-color: #F5F0EF; font-size: 12px; padding-top: 30px;  " class="" width="150%" align="center"><h5>Puede verificar la validez de este documento ingresando al portal web {{'enlinea/validacion-no-deudor/'.$codigo_externo }} 
                 o leyendo el siguiente código QR</h5>
             </td>

              <td colspan="2" height="60px" style=" border-top:0px;border-bottom:0px;border-left:0px;border-right:0px; background-color: #F5F0EF;"   align="center" width="50%">
                <img src="data:image/png;base64,{{ $qr_base64 }}" alt="QR Code"  />
             </td>
              
         </tr>
     </table>
  </footer>

    <?php $Nro = 1; ?>
   
    <div class="div">
      <table style="border-collapse: collapse;" width="100%">
        <tr>
          <th width="12%"><img src="{{ asset('img/logo4.png') }}" style="width: 100px"></th>
          <th width="8%"></th>
          <th width="56%">
              <p class="encabezado"  style="font-size: 16px !important">{{$info}}</p>              
              
          </th>
          <th width="22%"></th>
        </tr>
      </table>
    </div>
    <br>
    <p class="encabezado" style="font-size: 16px !important"><b>CERTIFICADO NO DEUDOR </b></p><br>
    <div class="div">

      <br>
			<p>AL: <b font style="text-transform: uppercase;">{{$fecha}}</b></p><br><br>
			
			<div style="text-align:justify; font-size: 100%">
				<p >La tesorería del {{$info}} CERTIFICA que el contribuyente <b font style="text-transform: uppercase;">{{$contribuyente}}</b> con identificación <b font style="text-transform: uppercase;">{{$cedula}}</b>,</b> no tiene valores pendientes al <b font style="text-transform: uppercase;">{{$fecha}}</b> por cancelar en este municipio, por lo cual se le otorga el certificado de no adeudar.
				</p><br>
        @if($codigo!="")
          <p>Con los siguientes códigos catastrales: <b> {{implode(', ', $codigo)}}</b> </p>
        @endif
        
		    
		    <br><br><br><br><br>
		    
     
    </div>
    
    <br>
    <br>
    <table width="100%">
      <tr>
        <th width="25%"></th>
        <th width="50%">
          <hr>
          <p class="encabezado1">FUNCIONARIO(A)</p>
        </th>
        <th width="25%"></th>
      </tr>
    </table>
    <br><br><br><br><br>
    <div>Documento válido por 30 días</div>
    
  </body>
  
</html>


