<?php
include_once 'Aduyng/Controller/Request/Translator.php';

class Aduyng_Controller_Request_Translator_Fetch extends Aduyng_Controller_Request_Translator {

    public function getConditions() {
        return $this->getArray('conditions');
    }

    /**
     * get page number. Return -1 if page number is invalid
     * @return int
     */
    public function getPageNumber() {
        return $this->getInteger('pageNumber');
    }

    /**
     * get number of record per page
     * @return type 
     */
    public function getNumberOfRecordsPerPage() {
        return $this->getPositiveInteger('numberOfRecordsPerPage');
    }

    /**
     * return the order array
     * @return array
     */
    public function getOrders() {
        return $this->getArray('orders');
    }

    public function getRelatedEntities() {
        return $this->getArray('relatedEntities');
    }
}