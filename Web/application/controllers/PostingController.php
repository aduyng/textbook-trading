<?php

include_once 'Aduyng/Controller/Action/Rest.php';
include_once 'models/Posting.php';


class PostingController extends Aduyng_Controller_Action_Rest {
	public function listAction(){
		try{
			$model = new PostingModel();
			$fetchRT = new Aduyng_Controller_Request_Translator_Fetch($this->_request);
			$fetchInputs = new Aduyng_Db_Table_FetchInput($fetchRT->getConditions(), $fetchRT->getOrders(), $fetchRT->getPageNumber(), $fetchRT->getNumberOfRecordsPerPage());
			$fetchOutputs = $model->fetch($fetchInputs);
			$this->view->fetchOutputs = $fetchOutputs;
			$this->setMessage("Postings have been retrieved successfully");
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

			$model = new PostingModel();
			$record = $model->fetchRowById($id);

			if( empty($record) ){
				throw new Exception("Id $id is not found");
			}

			$this->view->record = $record;
			$this->setMessage("Posting (id = $id) has been retrieved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function saveAction(){
		try{
			$id = $this->_translator->getPositiveInteger('id');

			$model = new PostingModel();
			if (empty($id)) {
				$row = $model->createRow();
			} else {
				$row = $model->fetchRowById($id);
				if (empty($row)) {
					throw new Exception("There is no posting with id = $id");
				}
			}
			
			$row->setSellerPhoneNumber($this->_translator->getString('sellerPhoneNumber'));
			$row->setTitle($this->_translator->getString('title'));
			$row->setIsbn($this->_translator->getString('isbn'));
			$row->setAuthors($this->_translator->getString('authors'));
			$row->setEdition($this->_translator->getString('edition'));
			$row->setPrice($this->_translator->getDouble('price'));
			$row->setAdditionalInfo($this->_translator->getString('additionalInfo'));
			$row->setPicture($this->_translator->getString('picture'));
			$row->markSold($this->_translator->getBool('isSold'));
			
			$row->save();
			$this->setMessage("Posting (id = {$row->getId()}) has been saved successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
	}

	public function removeAction(){
		try{
			$id = $this->_translator->getPositiveInteger('id');

			$model = new PostingModel();
			if (empty($id)) {
				throw new Exception("Id is required.");
			}
				
			$row = $model->fetchRowById($id);
			if (empty($row)) {
				throw new Exception("There is no posting with id = $id");
			}

			$row->delete();
			$this->setMessage("Posting (id = $id) has been deleted successfully");
			$this->setStatus(0);
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}

	}



}
