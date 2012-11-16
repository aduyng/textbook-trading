<?php
Zend_Loader::loadClass('Aduyng_View_Helper');
include_once 'models/Account.php';

class PostingHelper extends Aduyng_View_Helper{
    public function format(PostingRow $row){       
        $info = array(
            'id' => (int) $row->getId(),
            'sellerPhoneNumber' => $row->getSellerPhoneNumber(),
            'title' => $row->getTitle(),
            'isbn' => $row->getIsbn(),
            'authors' => $row->getAuthors(),
            'edition' => $row->getEdition(),
            'price' => (double)$row->getPrice(),
            'additionalInfo' => $row->getAdditionalInfo(),
            'picture' => base64_encode($row->getPicture()),
            'dateCreated' => (int)$row->getDateCreated()->toUnixTimestamp(),
            'flagsCount' => (int)$row->getFlagsCount(),
            'viewsCount' => (int)$row->getViewsCount(),
            'isSold' => $row->isSold(),
            'score' => (double)$row->getSeller()->getScore()
        );
        return $info;
        
    }
    
}
