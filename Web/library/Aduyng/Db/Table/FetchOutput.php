<?php

class Aduyng_Db_Table_FetchOutput {

    private $_numberOfRecords;
    private $_numberOfPages;
    private $_pageNumber;
    private $_data;

    public function getNumberOfRecords() {
        return $this->_numberOfRecords;
    }

    public function setNumberOfRecords($numberOfRecords) {
        $this->_numberOfRecords = $numberOfRecords;
    }

    public function getNumberOfPages() {
        return $this->_numberOfPages;
    }

    public function setNumberOfPages($numberOfPages) {
        $this->_numberOfPages = $numberOfPages;
    }

    public function getPageNumber() {
        return $this->_pageNumber;
    }

    public function setPageNumber($pageNumber) {
        $this->_pageNumber = $pageNumber;
    }

    public function getData() {
        return $this->_data;
    }

    public function setData($data) {
        $this->_data = $data;
    }

}