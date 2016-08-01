package net.madicorp.smartinvestplus.date;

import javax.validation.constraints.Pattern;

/**
 * User: sennen
 * Date: 29/07/2016
 * Time: 18:22
 */
@Pattern(regexp = "[12]?\\d{3}[01]\\d[012]\\d")
public @interface FormDate {
}

