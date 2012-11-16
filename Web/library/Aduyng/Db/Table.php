<?php

require_once 'Zend/Db/Table.php';
require_once 'Aduyng/Db/Table/FetchInput.php';
require_once 'Aduyng/Db/Table/FetchOutput.php';

abstract class Aduyng_Db_Table extends Zend_Db_Table{

    public function getName() {
        return $this->_name;
    }

    public function getTableName() {
        $config = $this->getAdapter()->getConfig();
        return $config['dbname'] . '.' . $this->getName();
    }

    public function getColumnName($columnName) {
        return $this->getTableName() . '.' . $columnName;
    }

    /**
     * Check if a field is unique
     * <code>
     *      //check of the uniqueness of the alias excluding the id
     *      $this->checkUniqueness(array('urlAlias' => $urlAlias), array('id' => $id));
     * </code>
     * 
     * @param array $values combination of values to check for uniqueness
     * @param array $excludingConditions conditions to eliminate records
     * @return boolean 
     */
    public function checkUniqueness($values = array(), $excludingConditions = array()) {
        $select = $this->select()->from($this, "COUNT(*) as nbRecords");

        foreach ($values as $field => $value) {
            $select->where("$field = ?", $value);
        }

        foreach ($excludingConditions as $field => $value) {
            if (!is_null($value)) {
                $select->where("$field != ?", $value);
            }
        }

        $row = $this->fetchRow($select);

        return ($row->nbRecords == 0);
    }

    /**
     *
     * @return array
     */
    public function getReferences() {
        return $this->_referenceMap;
    }

    /**
     * get primary key
     * @return array primary key
     */
    public function getPrimaryKey() {
        return array_values($this->info(self::PRIMARY));
    }

    /**
     * Automatically define table name according to class name
     * @return void
     */
    protected function _setupTableName() {
        $class = get_class($this);
        if (strstr($class, 'Mentis_Model_') !== false) {

            $this->_name = str_replace('Mentis_Model_', '', $class);
        }
        return parent::_setupTableName();
    }

    /**
     * Get the class name of the model
     * @return string
     */
    public function getClass() {
        return get_class($this);
    }

    /**
     * additional setup for Mentis model
     * <ul>
     *  <li>automatically set up the rowClass name if it is not</li>
     *  <li>automatically set up the rowsetClass name if it is not</li>
     * </ul>
     * @return void
     */
    public function init() {
        $entityClassName = str_replace('Model_', 'Model_Entity_', get_class($this));
        if (class_exists($entityClassName) && (empty($this->_rowClass) || $this->_rowClass == 'Zend_Db_Table_Row')) {
            $this->_rowClass = $entityClassName;
        }

        $entitysetClassName = str_replace('Model_', 'Model_EntitySet_', get_class($this));
        if (class_exists($entitysetClassName) && (empty($this->_rowsetClass) || $this->_rowsetClass == 'Zend_Db_Table_Rowset')) {
            $this->_rowsetClass = $entitysetClassName;
        }

        return parent::init();
    }

    /**
     * Call magic method. Currently support
     * <ul>
     *  <li>fetchRowBy&lt;ColumnName&gt[And&lt;ColumnName&gt]; to fetch a single row.</li>
     * <li>fetchAllBy&lt;ColumnName&gt[And&lt;ColumnName&gt]; to fetch a set of row</li>
     * </ul>
     * @param string $name function name
     * @param mixed $arguments
     * @return Zend_Db_Table_Row | Zend_Db_Table_Rowset 
     */
    public function __call($name, $arguments) {
        require_once 'Zend/Db/Table/Exception.php';

        $pattern = '/fetch(Row|All)?By(\w+)/';
        $matches = null;
        if (preg_match($pattern, $name, $matches)) {
            $conditions = explode('And', $matches[2]);

            $conditionCount = count($conditions);
            if (count($arguments) < $conditionCount) {
                throw new Zend_Db_Table_Exception("Number of arguments provided is not enough");
            }

            $fields = array();

            for ($i = 0; $i < $conditionCount; $i++) {
                $field = strtolower($conditions[$i][0]) . substr($conditions[$i], 1);
                $fields[$field] = $arguments[$i];
            }
            if ($matches[1] == 'Row') {
                return $this->fetchRowByConditions($fields);
            }

            return $this->fetchAllByConditions($fields);
        } 

        throw new Zend_Db_Table_Exception("Unrecognized method '$name()'");
    }

    protected function _buildConditions($conditions = array()) {
        if (!is_array($conditions)) {
            throw new Zend_Db_Table_Row_Exception("Conditions must be an associative array");
        }
        $select = $this->select();
        foreach ($conditions as $fieldName => $value) {
            if (is_array($value)) {
                $select->where("$fieldName IN (?)", $value);
            }elseif(($value instanceof Zend_Db_Expr) && $value->__toString() == 'NULL'){
                $select->where("$fieldName is null");
            } else {
                $select->where("$fieldName = ?", $value);
            }
        }
        return $select;
    }

    public function fetchRowByConditions($conditions = array()) {
        $select = $this->_buildConditions($conditions);
        return $this->fetchRow($select);
    }

    public function fetchAllByConditions($conditions = array()) {
        $select = $this->_buildConditions($conditions);

        return $this->fetchAll($select);
    }

    public function quoteInto() {
        $args = func_get_args();
        $count = count($args);
        if ($count == 0) {
            return null;
        }

        if ($count == 1) {
            return $args[0];
        }

        $sql = $args[0];


        for ($i = 1; $i < $count; $i++) {
            $sql = $this->getAdapter()->quoteInto($sql, $args[$i]);
        }

        return $sql;
    }

 
    public function count() {
        $select = $this->select()->from($this, array('numberOfRecords' => 'COUNT(*)'));
        return $this->fetchOne($select);
    }

    public function truncate() {
        return $this->delete('');
    }

    /**
     * fetch function
     * @author Duy A. Nguyen duyanh@uta.edu http://duy-nguyen.info
     */
    public function fetch(Aduyng_Db_Table_FetchInput $inputs) {
        $countSelect = $this->_createCountSelectForFetch($inputs->getConditions());
        $countSelect->reset(Zend_Db_Select::COLUMNS);
        $countSelect->from($this, "COUNT(*) as nbRecords");
        
        $result = $this->fetchRow($countSelect);
        $numberOfRecords = $result->nbRecords;

        $select = $this->_createSelectForFetch($inputs->getConditions(), $inputs->getOrders());
        
        $pageNumber = $inputs->getPageNumber();
        $nbRecordsPerPage = $inputs->getNumberOfRecordsPerPage();
        
        if ($numberOfRecords > 0) {
            $numberOfPages = ceil($numberOfRecords / $nbRecordsPerPage);
        } else {
            $numberOfRecords = 0;
            $numberOfPages = 0;
        }
        if ($pageNumber > $numberOfPages - 1) {
            $pageNumber = $numberOfPages - 1;
        }

        if (is_numeric($pageNumber) && $pageNumber >= 0 && is_numeric($nbRecordsPerPage) && $nbRecordsPerPage > 0) {
            $select->limit($nbRecordsPerPage, $pageNumber * $nbRecordsPerPage);
        }

        $rows = $this->fetchAll($select);

        $output = new Aduyng_Db_Table_FetchOutput();
        $output->setNumberOfRecords($numberOfRecords);
        $output->setNumberOfPages($numberOfPages);
        $output->setPageNumber($pageNumber);
        $output->setData($rows);

        return $output;
    }

    /**
     * Create select for fetch function
     * @param type $conditions
     * @return type 
     */
    protected function _createSelectForFetch($conditions = array(), $orders = array()) {
        $select = $this->select();

        if (is_array($conditions) && count($conditions) > 0) {

            foreach ($conditions as $field => $value) {
                if (!is_null($value)) {
                    $select->where("$field = ?", $value);
                } else {
                    $select->where("$field = ?", new Zend_Db_Expr('NULL'));
                }
            }
        }

        if (is_array($orders)) {
            foreach ($orders as $field => $direction) {
                if (empty($field)) {
                    continue;
                }
                if (strcasecmp($direction, 'asc') != 0 && strcasecmp($direction, 'desc') != 0) {
                    $direction = 'asc';
                }
                $select->order("$field $direction");
            }
        }
        //var_dump($select); exit;
        return $select;
    }
    
    protected function _createCountSelectForFetch($conditions = array()){
        return $this->_createSelectForFetch($conditions);
    }

    

}

