<?php

class Bootstrap extends Zend_Application_Bootstrap_Bootstrap {

    public function _initApplication() {
        Zend_Registry::set('config', $this->getOptions());
        
        Zend_Date::setOptions(array('format_type' => 'php'));
    }

    protected function _initLog() {
        if ($this->hasPluginResource("log")) {
            $r = $this->getPluginResource("log");
            $log = $r->getLog();
            Zend_Registry::set("log", $log);
        }
    }

}

