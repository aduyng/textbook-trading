<?php

Zend_Loader::loadClass('Aduyng_Controller_Action');

class ErrorController extends Aduyng_Controller_Action {

    public function errorAction() {

        $errors = $this->_getParam('error_handler');

        if (!$errors || !$errors instanceof ArrayObject) {
            $this->view->message = 'You have reached the error page';
            return;
        }

        switch ($errors->type) {
            case Zend_Controller_Plugin_ErrorHandler::EXCEPTION_NO_ROUTE:
            case Zend_Controller_Plugin_ErrorHandler::EXCEPTION_NO_CONTROLLER:
            case Zend_Controller_Plugin_ErrorHandler::EXCEPTION_NO_ACTION:
                // 404 error -- controller or action not found
            	$this->getResponse()->setHttpResponseCode(404);
                $priority = Zend_Log::NOTICE;
                $this->view->message = 'Not Found';

                break;
            
            default:
                // application error
                $this->getResponse()->setHttpResponseCode(500);
                $priority = Zend_Log::CRIT;
                $this->view->message = 'Application error';
                break;
        }


        // Log exception, if logger available
        if (($log = Zend_Registry::get('log'))) {
            $log->log($errors->exception->getMessage(), $priority, $errors->exception);
        }

        // conditionally display exceptions
        if ($this->getInvokeArg('displayExceptions') == true) {
            $this->view->exception = $errors->exception;
        }

        $this->view->request = $errors->request;
    }

//    public function getLog() {
//        $bootstrap = $this->getInvokeArg('bootstrap');
//        if (!$bootstrap->hasResource('log')) {
//            return false;
//        }
//        $log = $bootstrap->getResource('log');
//        return $log;
//    }

}

