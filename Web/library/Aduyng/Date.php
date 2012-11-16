<?php

/*
 * 
 * 
 */

/**
 * Description of Date
 *
 * @author duyanh
 */
class Aduyng_Date extends Zend_Date {
    const SQL_DATETIME_FORMAT = 'Y-m-d H:i:s';

    public function toFullFormat() {
        return $this->toString('l, j F, Y');
    }

    public function toSqlFormat() {
        return $this->toString('Y-m-d H:i:s');
    }

    public static function fromSqlFormatToFullFormat($sqlDateValue) {
        $date = new Aduyng_Date($sqlDateValue);
        return $date->toFullFormat();
    }

    public static function fromFullFormatToSqlFormat($fullDateValue) {
        $date = new Aduyng_Date($fullDateValue);
        return $date->toSqlFormat();
    }

    public function toUnixTimestamp() {
        return $this->toString('U');
    }

    public function toJavascriptTimestamp() {
        return ((int) $this->toUnixTimestamp()) * 1000;
    }

    public function toString($format = null, $type = null, $locale = null) {
        if ($format == null) {
            return parent::toString('m/d/Y H:i:s', $type, $locale);
        }
        return parent::toString($format, $type, $locale);
    }

    public function __toString() {
        return $this->toString();
    }

    public function toRelativeTime($includeTime = true) {
        $diff = time() - $this->getTimestamp();
        if ($diff > 0) {
            if ($diff < 60)
                return $diff . ' second' . ($diff > 1 ? 's' : '') . ' ago';
            $diff = round($diff / 60);
            if ($diff < 60)
                return $diff . ' minute' . ($diff > 1 ? 's' : '') . ' ago';
            $diff = round($diff / 60);
            if ($diff < 24)
                return $diff . ' hour' . ($diff > 1 ? 's' : '') . ' ago';
            $diff = round($diff / 24);
            if ($diff < 7)
                return $diff . ' day' . ($diff > 1 ? 's' : '') . ' ago';
            $diff = round($diff / 7);
            if ($diff < 4)
                return $diff . ' week' . ($diff > 1 ? 's' : '') . ' ago';
            return 'on ' . $this;
        } else {
            if ($diff > -60)
                return 'in ' . -$diff . ' second' . (-$diff > 1 ? 's' : '');
            $diff = round($diff / 60);
            if ($diff > -60)
                return 'in ' . -$diff . ' minute' . (-$diff > 1 ? 's' : '');
            $diff = round($diff / 60);
            if ($diff > -24)
                return 'in ' . -$diff . ' hour' . (-$diff > 1 ? 's' : '');
            $diff = round($diff / 24);
            if ($diff > -7)
                return 'in ' . -$diff . ' day' . (-$diff > 1 ? 's' : '');
            $diff = round($diff / 7);
            if ($diff > -4)
                return 'in ' . -$diff . ' week' . (-$diff > 1 ? 's' : '');
            return 'on ' . ($includeTime? $this : $this->toString('m/d/Y'));
        }
    }

}

?>
