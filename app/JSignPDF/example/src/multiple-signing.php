<?php

require_once('../../src/pdf-sign-cli.php');


$pdfSigner =  new BSrE_PDF_Signer_Cli();

$pdfSigner->setDocument('../pdf/example.pdf');

$pdfSigner->readCertificateFromFile(
    '../cert/devel-desember2019.p12',
    '1234'                
);

$pdfSigner->setDirectory(
    'example/pdf/output',   // Nama Direktori dimulai dari root 
    true                    // True untuk membuat direktori baru, jika tidak ada
);

$pdfSigner->setSuffixFileName('');  // default : '_signed'

if(!$pdfSigner->sign()) 
    echo $pdfSigner->getError();

/**
 * Proses tanda tangan Kedua
 */ 

$pdfSigner->setDocument('../pdf/output/example.pdf');

$pdfSigner->setSuffixFileName('_signed');

$pdfSigner->addmultipleSign();

if(!$pdfSigner->sign()) 
    echo $pdfSigner->getError();