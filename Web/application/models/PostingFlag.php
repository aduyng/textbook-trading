<?php
include_once 'Aduyng/Db/Table.php';
include_once 'models/rows/PostingFlag.php';
include_once 'models/rowsets/PostingFlag.php';

class PostingFlagModel extends Aduyng_Db_Table{
	protected $_name = 'PostingFlag';
	protected $_rowClass = 'PostingFlagRow';
	protected $_rowsetClass = 'PostingFlagRowset';


	protected $_referenceMap = array(
        'Posting' => array(
                	'columns'           => 'postingId',
                    'refTableClass'     => 'PostingModel',
                    'refColumns'        => 'id'
	),
    	'Flagger' => array(
                    	'columns'           => 'flaggerPhoneNumber',
                        'refTableClass'     => 'AccountModel',
                        'refColumns'        => 'phoneNumber'
	)
	);
}
