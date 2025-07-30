# BSrE-PDF-Sign-Cli

BSrE-PDF-Sign is a PHP Library to perform Digital Signature on Portable Document Format (PDF).

## Requirements

+ System Call Enabled (Shell_exec or Exec Function)
+ Installed JDK Min. Version 1.7 (Java Environment)
+ Internet Connection (allow connection to specific url)

## Example

```php
<?php

require_once('../../src/pdf-sign-cli.php');
 
 
$pdfSigner =  new BSrE_PDF_Signer_Cli();

$pdfSigner->setLibraryPath('path/to/dir/library');

$pdfSigner->setDocument('path/to/pdf');

$pdfSigner->readCertificateFromFile(
    'path/to/p12file',
    'passphrase'            
);

if(!$pdfSigner->sign()) 
    echo $pdfSigner->getError();


