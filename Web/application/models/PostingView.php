<?php
include_once 'Aduyng/Db/Table.php';
include_once 'models/rows/PostingView.php';
include_once 'models/rowsets/PostingView.php';

class PostingViewModel extends Aduyng_Db_Table{
	protected $_name = 'PostingView';
	protected $_rowClass = 'PostingViewRow';
	protected $_rowsetClass = 'PostingViewRowset';


	protected $_referenceMap = array(
        'Posting' => array(
                	'columns'           => 'postingId',
                    'refTableClass'     => 'PostingModel',
                    'refColumns'        => 'id'
	),
    	'Viewer' => array(
                    	'columns'           => 'viewerPhoneNumber',
                        'refTableClass'     => 'AccountModel',
                        'refColumns'        => 'phoneNumber'
	)
	);
}
