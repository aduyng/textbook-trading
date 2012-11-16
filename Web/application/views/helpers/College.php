<?php
Zend_Loader::loadClass('Aduyng_View_Helper');
class CollegeHelper extends Aduyng_View_Helper{
    public function format(CollegeRow $row){
        
        return array(
            'id' => $row->getId(),
            'name' => $row->getName(),
            'logo' => base64_encode($row->getLogo())
        );
    }
    
}
