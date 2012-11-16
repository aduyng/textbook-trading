<?php

include_once 'Aduyng/Controller/Action/Rest.php';
include_once 'models/College.php';


class CollegeController extends Aduyng_Controller_Action_Rest {
	public function listAction(){
		try{
			$model = new CollegeModel();
			$fetchRT = new Aduyng_Controller_Request_Translator_Fetch($this->_request);
			$fetchInputs = new Aduyng_Db_Table_FetchInput($fetchRT->getConditions(), $fetchRT->getOrders(), $fetchRT->getPageNumber(), $fetchRT->getNumberOfRecordsPerPage());
			$fetchOutputs = $model->fetch($fetchInputs);
			$this->view->fetchOutputs = $fetchOutputs;
			$this->setMessage("Colleges have been retrieved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function detailAction(){
		try{
	
			$id = $this->_translator->getPositiveInteger('id');

			if( empty($id)){
				throw new Exception("Id is required");
			}

			$model = new CollegeModel();
			$record = $model->fetchRowById($id);

			if( empty($record) ){
				throw new Exception("Id $id is not found");
			}

			$this->view->record = $record;
			$this->setMessage("College (id = $id) has been retrieved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function saveAction(){
		try{
			$id = $this->_translator->getPositiveInteger('id');

			$model = new CollegeModel();
			if (empty($id)) {
				$row = $model->createRow();
			} else {
				$row = $model->fetchRowById($id);
				if (empty($row)) {
					throw new Exception("There is no row with id = $id");
				}
			}
			
			$row->setName($this->_translator->getString('name'));
			$row->setLogo($this->_translator->getString('logo'));

			$row->save();
			$this->setMessage("College (id = {$row->getId()}) has been saved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function removeAction(){
		try{
			$id = $this->_translator->getPositiveInteger('id');

			$model = new CollegeModel();
			if (empty($id)) {
				throw new Exception("Id is required.");
			}
				
			$row = $model->fetchRowById($id);
			if (empty($row)) {
				throw new Exception("There is no row with id = $id");
			}

			$row->delete();
			$this->setMessage("College (id = $id) has been deleted successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}

	}



}
