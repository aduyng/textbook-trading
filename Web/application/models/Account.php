<?php
include_once 'Aduyng/Db/Table.php';
include_once 'models/rows/Account.php';
include_once 'models/rowsets/Account.php';

class AccountModel extends Aduyng_Db_Table{
    protected $_name = 'Account';
    protected $_rowClass = 'AccountRow';
    protected $_rowsetClass = 'AccountRowset';

    
    protected $_referenceMap = array(
    		'College' => array(
            	'columns'           => 'collegeId',
                'refTableClass'     => 'CollegeModel',
                'refColumns'        => 'id'
    )
    );
}
