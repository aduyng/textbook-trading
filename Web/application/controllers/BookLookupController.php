<?php

include_once 'Aduyng/Controller/Action/Rest.php';

class BookLookupController extends Aduyng_Controller_Action_Rest {

	public function lookupAction() {
		try{
			$query = $this->_translator->getString('query');

			if( empty($query) ){
				throw new Exception("query must not be empty.");
			}

			include_once 'Zend/Http/Client.php';
			$client = new Zend_Http_Client('http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Dstripbooks&field-keywords=' . urlencode($query));
			$response = $client->request();

			$body = $response->getBody();

			$pattern = '%<div class="title">.*?</div>%sm';
			$books = array();
			if (preg_match_all($pattern, $body, $matches)) {
				$c = count($matches[0]);
				for ($i = 0; $i < $c; $i++) {

					$line = $matches[0][$i];

					$pattern = '/[\r\n\t]/';
					$line = preg_replace($pattern, '', $line);
					$pattern = '/\s{2,}/';
					$line = preg_replace($pattern, ' ', $line);
					if(preg_match('%<a.*?(\d{10,}).*?>(.+?)</a>.*?<span class="ptBrand">by (.+?)</span>%', $line, $lineMatches)){
						$isbn = trim($lineMatches[1]);
						$title = trim(html_entity_decode(strip_tags($lineMatches[2])));


						$authors = preg_split('/,|(and)/', strip_tags(html_entity_decode($lineMatches[3])));
						foreach($authors as $index => $author){
							$authors[$index] = trim($author);
						}
						$authors = implode(', ', $authors);

						//detect edition in the title
						if(preg_match('/\(?((\d+)(th|st|nd|rd)?) Edition\)?/i', $title, $editionMatches)){
							$edition = $editionMatches[1];
							$title = trim(str_replace($editionMatches[0], '', $title));
						}else{
							$edition = "";
						}
						
						

						$books[] = compact('isbn', 'title', 'authors', 'edition');

					}

				}
				$this->setMessage("Books have been looked up successfully");
				$this->setStatus(0);
			}
		}catch (Exception $e){
			$this->setMessage($e->getMessage());
			$this->setStatus(-1);
		}
		$this->view->records = $books;
	}

}
