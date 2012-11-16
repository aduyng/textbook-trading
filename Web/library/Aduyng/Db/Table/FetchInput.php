<?php

class Aduyng_Db_Table_FetchInput {

    private $_conditions = array();
    private $_orders = array();
    private $_pageNumber = 0;
    private $_numberOfRecordsPerPage = 20;
    
    function __construct($conditions = array(), $orders = array(), $pageNumber = 0, $numberOfRecordsPerPage = 20) {
        $this->_conditions = $conditions;
        $this->_orders = $orders;
        $this->setPageNumber($pageNumber);
        $this->setNumberOfRecordsPerPage($numberOfRecordsPerPage);
    }

    public function getConditions() {
        return $this->_conditions;
    }

    public function setConditions($conditions) {
        $this->_conditions = $conditions;
    }

    public function getOrders() {
        return $this->_orders;
    }

    public function setOrders($orders) {
        $this->_orders = $orders;
    }

    public function getPageNumber() {
        
        return $this->_pageNumber;
    }

    public function setPageNumber($pageNumber) {
        
        if (!is_numeric($pageNumber) || $pageNumber < 0) {
            $pageNumber = 0;
        } else {
            $pageNumber = (int) $pageNumber;
        }
        
        $this->_pageNumber = $pageNumber;
    }

    public function getNumberOfRecordsPerPage() {
        return $this->_numberOfRecordsPerPage;
    }

    public function setNumberOfRecordsPerPage($numberOfRecordsPerPage) {
        if (!is_numeric($numberOfRecordsPerPage) || $numberOfRecordsPerPage <= 0) {
            $numberOfRecordsPerPage = 10;
        } else {
            $numberOfRecordsPerPage = (int) $numberOfRecordsPerPage;
        }
        
        $this->_numberOfRecordsPerPage = $numberOfRecordsPerPage;
    }



}