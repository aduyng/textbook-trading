<?php

include_once('Aduyng/Date.php');

class Aduyng_Db_Table_Row extends Zend_Db_Table_Row {

    public static function isEmpty($value) {
        if ($value instanceof Zend_Db_Expr && $value->__toString() == "NULL") {
            return true;
        }
        if (is_string($value)) {
            return strlen(trim($value)) == 0;
        }

        return empty($value);
    }

    /**
     * Get the id of the row
     * @return mixed | int 
     */
    public function getId() {
        return $this->id;
    }

    /**
     * convert all the data of the row to string
     * @return string
     */
    public function __toString() {
        return '{' . implode(',', $this->_data) . '}';
    }

    /**
     * Check if the entity has created but has not been saved yet
     * @return bool
     */
    public function isNewlyCreated() {
        return empty($this->_cleanData);
    }

    /**
     * Transform columnName to the proper format
     * @param string $columnName
     * @return string
     */
    protected function _transformColumn($columnName) {
        return strtolower($columnName[0]) . substr($columnName, 1);
    }

    /**
     * get magic method.
     * <code>
     *      $model = new Aduyng_Model_User();
     *      $user = $model->fetchAll()->current();
     * 
     *      //get all emails belong to this user ('Aduyng_Model_UserEmail' is included in Aduyng_Model_User's dependentTables)
     *      $userEmails = $row->getUserEmail();
     * 
     * 
     *      $model = new Aduyng_Model_UserEmail();
     *      $userEmail = $model->fetchAll()->current();
     *      //get user object from userEmail (UserEmail referenceMap has to include User model)
     *      $userEmail->getUser();
     * </code>
     * 
     * @param type $columnName
     * @return type 
     * @author Duy A. Nguyen (duyanh@uta.edu)
     */
    public function __get($columnName) {
        

        try {
            $value = parent::__get($columnName);
            $columnType = strtolower($this->getColumnSqlType($columnName));
            switch ($columnType) {
                case 'timestamp':
                    if (empty($value) || ($value instanceof Zend_Db_Expr && $value->__toString() == 'NULL')) {
                        return null;
                    }
                    return new Aduyng_Date(strtotime($value));
                    
                default:
                    return $value;
            }
        } catch (Zend_Db_Table_Row_Exception $e) {
        	
            //capitalize the first character of the columnName
//             $col = $this->_transformColumn($columnName);

            //getting dependent rowset
            $dependentTables = $this->getTable()->getDependentTables();
            
            foreach ($dependentTables as $dependentTable) {
                if (preg_match('/^' . $columnName . 'Model$/', $dependentTable)) {
                    return $this->findDependentRowset($dependentTable);
                }
            }

            //getting parent row
            $referenceMap = $this->getTable()->getReferences();
//             var_dump($referenceMap);exit();
            foreach ($referenceMap as $rule => $map) {
//             	echo $rule . "," . $map[Zend_Db_Table_Abstract::REF_TABLE_CLASS];
                if ($col === $rule || preg_match('/^' . $columnName . 'Model$/', $map[Zend_Db_Table_Abstract::REF_TABLE_CLASS])) {
                    return $this->findParentRow($map[Zend_Db_Table_Abstract::REF_TABLE_CLASS]);
                }
            }
//             exit();

            throw $e;
        }
        
    }

    /**
     * set magic method.
     * <code>
     *  $model = new Aduyng_Model_UserEmail();
     *  $row = $model->createRow();
     *  
     *  $userModel = new Aduyng_Model_User();
     *  $userRow = $userModel->fetchAll()->current();
     *  
     *  $row->setUser($userRow);
     *  $row->setAddress('example@phpunit.net');
     *  $row->setType(Aduyng_Model_Entity_UserEmail::TYPE_PERSONAL);
     *  $row->setIsPrimary(false);
     *  
     *  $row->save();
     * </code>
     * 
     * @param type $columnName
     * @return Zend_Db_Table_Row_Abstract 
     */
    public function __set($columnName, $value) {
        try {
            if (is_null($value)) {
                $val = new Zend_Db_Expr("NULL");
            } elseif ($value instanceof Aduyng_Date) {
                if ($this->getColumnSqlType($columnName) == "timestamp") {

                    $val = $value->toSqlFormat();
                }
            } else {
                $val = $value;
            }

            parent::__set($columnName, $val);
        } catch (Zend_Db_Table_Row_Exception $e) {

            $col = $this->_transformColumn($columnName) . 'Id';

            if ($value instanceof Zend_Db_Table_Row_Abstract) {
                $value = $value->getId();
            }

            parent::__set($col, $value);
        }
        return $this;
    }

    protected function getColumnSqlType($columnName) {
        $col = $this->_transformColumn($columnName);
        $columnMetadata = $this->getTable()->info(Zend_Db_Table_Abstract::METADATA);
        return isset($columnMetadata[$col]['DATA_TYPE']) ? $columnMetadata[$col]['DATA_TYPE'] : null;
    }

    /**
     * Call magic method.
     * Some examples of using call
     * <code>
     *      $model = new Aduyng_Model_User();
     *      $user = $model->fetchAll()->current();
     *      //calling getDisplayName (undefined) where displayName is a column in the table
     *      $displayName = $user->getDisplayName();
     * 
     *      //calling setDisplayName (undefined) where displayName is a column in the table
     *      $user->setDisplayName('abc');
     * </code>
     * @param type $method
     * @param array $args
     * @return Aduyng_Model_Entity_Abstract 
     */
    public function __call($method, array $args) {
        $matches = array();
        //handling the setter
        if (preg_match('/^set([A-Z]\w+?)$/', $method, $matches)) {
            $this->{$matches[1]} = $args[0];
            return $this;
        }

        //handling the getter
        if (preg_match('/^get([A-Z]\w+?)$/', $method, $matches)) {
            return $this->{$matches[1]};
        }

        //handling boolean getter
        if (preg_match('/^is([A-Z]\w+?)$/', $method, $matches)) {
            $retVal = $this->{$method};
            return!(empty($retVal) || is_null($retVal));
        }

        //handling boolean setter
        if (preg_match('/^mark([A-Z]\w+?)$/', $method, $matches)) {
            $method = 'is' . $matches[1];
            $setVal = is_null($args[0]) || empty($args[0]) ? 0 : 1;
            $this->{$method} = $setVal;
            return $this;
        }

        return parent::__call($method, $args);
    }

    public function equals(Aduyng_Model_Entity_Abstract $entity) {
        return get_class($this) === get_class($entity) && $entity->getId() == $this->getId();
    }

}