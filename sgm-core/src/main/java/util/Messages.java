/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author CarlosLoorVargas
 */
public class Messages {

    public static String credencialesInvalidas = "Usuario o Contraseña Incorrecta.";
    public static String userSinCorreo = "Este usuario NO tiene correo ingresado.";
    public static String correoNoEnviado = "NO se pudo enviar correo.";
    public static String sinCoincidencias = "No se encontraron coincidencias.";
    public static String faltanCampos = "Faltan de llenar Campos.";
    public static String longitudClave = "Longitud de clave mínimo 8 caracteres.";
    public static String noCoincidenClaves = "No coinciden las claves.";
    public static String problemaConexion = "Problemas de conexión, intente mas tarde.";
    public static String enteExiste = "El usuario ya existe.";
    public static String enteNoExiste = "El usuaro no existe. Proceda a crearlo por favor.";
    public static String error = "Error. Comuníquese con Sistemas.";
    public static String cedulaCIinvalida = "La Cédula/RUC ingresada no Existe o es Inválida .";
    public static String correoInvalido = "El correo ingresado es inválido.";
    public static String tlfnInvalido = "El número de teléfono ingresado es inválido.";
    public static String campoVacio = "El campo está vacío.";
    public static String elementoRepetido = "Elemento ingresado está repetido.";
    public static String ciRucExiste = "Número de CI/RUC ya está registrado.";
    public static String noListaVacia = "La lista no puede estar vacía.";
    public static String noAsignadoPersona = "Persona no asignada.";
    public static String userDisponible = "Usuario se encuentra disponible.";
    public static String guardadoExitoso = "Elemento guardado con éxito.";
    public static String noEliminar = "Elemento NO se puede eliminar.";
    public static String valorIncorrecto = "El valor ingresado no es Correcto.";
    public static String elementoInactivo = "Estado de elemento seleccionado es Inactivo.";
    public static String correosVacios = "El campo de los Correos no puede estar vacio.";
    public static String unSoloElemento = "Sólo se permite asociar un elemento.";
    public static String tamanioCorto = "Tamanio de campo Código debe ser más corto.";
    public static String enteAsociadoExiste = "El ente asociado ya existe.";
    public static String fechaInvalida = "Fecha Caducidad debe ser mayor a Fecha Actual.";
    
    //MENSAJES DE RECAUDACIONES
    public static String liquidacionCancelada = "La liquidación ya está cancelada.";
    public static String liquidacionAnulada = "La liquidación fue anulada.";
    public static String liquidacionNOpagada = "La liquidacion aun no ha sido pagada.";

    //MNESAJES DE TRAMITES
    public static String documentos = "Debe ingresar los documento(s) solicitado(s).";
    public static String observaciones = "Debe ingresar las Observaciones.";
    public static String faltaSubirDocumento = "No ha subido ningun Documento.";
    public static String transacError = "Ha ocurrido un error al momento de realizar la transacción, intente nuevamente.";
    public static String sinPrediosSuf = "Para iniciar el tramite, es necesario contar con mas de un predio";
    public static String sinAvaluoPredio = "El predio debe tener los datos del avaluo respectivo";
    public static String predioFueraSector = "El predio seleccionado debe estar dentro del sector del predio referencial";
    public static String sinPrediosPC = "No tiene predios";
    public static String noCumpleRequisitos = "No cumpple con los requisitos para proceder.";
    public static String prediosNoPH = "Predio ya es parte de una Propiedad Horizontal";
    public static String errorTransaccion = "Error En Transacción";
    public static String transacExitosa ="Transaccion Exitosa.";
    public static String campoVacioONulo = "Campo no puede ser nulo ni menor a cero.";
    public static String PredioNoGrabado = "Tiene que guardar primero el predio para poder continuar con esta sección. ";
    public static String predioTramiteActivo = "Predio ya tiene un Trámite Activo.";

    //MNESAJES DE BPM
    public static String iniciarProcesoError = "El trámite solicitado, no se pudo iniciar, verifique los requisitos.";
    public static String iniciarProcesoOk = "El trámite solicitado, ha sido iniciado satisfactoriamente.";
    
    //MENSAJES REGISTRO
    public static String bitacoraFicha = "Consulta de Bitacora por Numero de Ficha Registral";
    public static String bitacoraMovimiento = "Consulta de Bitacora por Inscripcion";
    

}
