<?php

require_once('../../src/pdf-sign-cli.php');
 

$pdfSigner =  new BSrE_PDF_Signer_Cli();

$pdfs = array(
    '../pdf/example.pdf',
    '../pdf/example2.pdf'
);

$pdfSigner->setDocument($pdfs);

$pdfSigner->readCertificateFromFile(
    '../cert/devel-desember2019.p12',
    '1234'                 
);

if(!$pdfSigner->sign()) 
    echo $pdfSigner->getError();