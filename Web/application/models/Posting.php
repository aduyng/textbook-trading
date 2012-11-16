<?php
include_once 'Aduyng/Db/Table.php';
include_once 'models/rows/Posting.php';
include_once 'models/rowsets/Posting.php';

class PostingModel extends Aduyng_Db_Table{
    protected $_name = 'Posting';
    protected $_rowClass = 'PostingRow';
    protected $_rowsetClass = 'PostingRowset';
    
    protected $_dependentTables = array('PostingFlag', 'PostingView');
    
    protected $_referenceMap = array(
        		'Seller' => array(
                	'columns'           => 'sellerPhoneNumber',
                    'refTableClass'     => 'AccountModel',
                    'refColumns'        => 'phoneNumber'
    )
    );
}
