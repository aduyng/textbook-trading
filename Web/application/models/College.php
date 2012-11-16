<?php
include_once 'Aduyng/Db/Table.php';
include_once 'models/rows/College.php';
include_once 'models/rowsets/College.php';

class CollegeModel extends Aduyng_Db_Table{
	protected $_name = 'College';
	protected $_rowClass = 'CollegeRow';
	protected $_rowsetClass = 'CollegeRowset';
	
	protected $_dependentTables = array('Account');

	
}
