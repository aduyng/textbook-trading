<?php
Zend_Loader::loadClass('Aduyng_View_Helper');
class AccountHelper extends Aduyng_View_Helper{
    public function format(AccountRow $row){
        
        return array(
            'phoneNumber' => $row->getPhoneNumber(),
            'isTextable' => $row->isTextable(),
            'isCallable' => $row->isCallable(),
            'collegeId' => (int) $row->getCollegeId(),
            'score' => (double)$row->getScore()
        );
    }
    
}
