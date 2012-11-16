<?php

/**
 * @author Duy A. Nguyen duyanh@uta.edu http://duy-nguyen.info
 *  
 */
include_once 'Aduyng/View/Helper.php';
class Aduyng_View_Helper_Fetch extends Aduyng_View_Helper {

    public function format(Aduyng_Db_Table_FetchOutput $result, $rowClassTranslatorHelper, $relatedEntities = array()) {
        $response = array();
        $response['numberOfRecords'] = 0;
        $response['numberOfPages'] = 0;
        $response['pageNumber'] = 0;
        $response['records'] = array();

        if (!empty($result)) {
            $response['numberOfRecords'] = (int) $result->getNumberOfRecords();
            $response['numberOfPages'] = (int) $result->getNumberOfPages();
            $response['pageNumber'] = (int) $result->getPageNumber();

            $records = $result->getData();
           
            foreach ($records as $index => $record) {
                $response['records'][$index] = $rowClassTranslatorHelper->format($record, $relatedEntities);
            }
        }

        return $response;
    }

}