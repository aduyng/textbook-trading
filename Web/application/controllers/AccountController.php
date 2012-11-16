<?php

include_once 'Aduyng/Controller/Action/Rest.php';
include_once 'models/Account.php';


class AccountController extends Aduyng_Controller_Action_Rest {
	public function listAction(){
		try{
			$model = new AccountModel();
			$fetchRT = new Aduyng_Controller_Request_Translator_Fetch($this->_request);
			$fetchInputs = new Aduyng_Db_Table_FetchInput($fetchRT->getConditions(), $fetchRT->getOrders(), $fetchRT->getPageNumber(), $fetchRT->getNumberOfRecordsPerPage());
			$fetchOutputs = $model->fetch($fetchInputs);
			$this->view->fetchOutputs = $fetchOutputs;
			$this->setMessage("Accounts have been retrieved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function detailAction(){
		try{

			$id = $this->_translator->getString('phoneNumber');

			if( empty($id)){
				throw new Exception("phoneNumber is required");
			}

			$model = new AccountModel();
			$record = $model->fetchRowByPhoneNumber($id);

			if( empty($record) ){
				throw new Exception("Phone number $id is not found");
			}

			$this->view->record = $record;
			$this->setMessage("Account (phoneNumber = $id) has been retrieved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function saveAction(){
		try{
			$id = $this->_translator->getString('phoneNumber');

			$model = new AccountModel();
			if (empty($id)) {
				throw new Exception("phoneNumber is required.");
			}
				
			$row = $model->fetchRowByPhoneNumber($id);
			if (empty($row)) {
				$row = $model->createRow();
				$row->setPhoneNumber($id);
			}

			$row->markTextable($this->_translator->getBool('isTextable'));
			$row->markCallable($this->_translator->getBool('isCallable'));
			$row->setCollegeId($this->_translator->getString('collegeId'));

			$row->save();
			$this->setMessage("Account (id = {$row->getPhoneNumber()}) has been saved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function removeAction(){
		try{
			$id = $this->_translator->getString('phoneNumber');

			$model = new AccountModel();
			if (empty($id)) {
				throw new Exception("Id is required.");
			}

			$row = $model->fetchRowByPhoneNumber($id);
			if (empty($row)) {
				throw new Exception("There is no account with phoneNumber = $id");
			}

			$row->delete();
			$this->setMessage("Account (id = $id) has been deleted successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}

	}



}
