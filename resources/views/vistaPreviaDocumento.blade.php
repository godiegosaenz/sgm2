<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    
    @isset($documentName)
        <title>{{$documentName}}</title>
    @else
        <title>Documento</title>
    @endisset

    <style type="text/css">
        iframe{
            margin: 0 !important;
            padding: 0 !important;
            border: 0 !important;
        }
        body{
            margin: 0 !important;
            padding: 0 !important;
            border: 0 !important;
            background-color: rgb(82, 86, 89);
        }
    </style>
</head>
<body>
    @isset($documentEncode)
        <iframe id="iframe_document" src="data:application/pdf;base64,{{$documentEncode}}" type="application/pdf" frameborder="0" style="width: 100%;"></iframe>
    @endisset
    
    <!-- jQuery 3 -->
    <script src="{{ asset('bower_components/jquery/dist/jquery.min.js') }}"></script>
    <!-- jQuery UI 1.11.4 -->
    <script src="{{ asset('bower_components/jquery-ui/jquery-ui.min.js') }}"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var alto= $(window).height()-5;
            $("#iframe_document").css("height",alto+"px");
        });
    </script>
</body>
</html>