<?php
$frontController = Zend_Controller_Front::getInstance();
$request = $frontController->getRequest();

$clientCallback = $request->getParam('callback');
$resp = array('status' => $this->status, 'message' => $this->message);
if( !empty($this->response)){
	$resp = array_merge($resp, $this->response);
}


if (!empty($clientCallback)) {
	echo $clientCallback . '(';
}

echo $this->json($resp);

if (!empty($clientCallback)) {
	echo ')';
}