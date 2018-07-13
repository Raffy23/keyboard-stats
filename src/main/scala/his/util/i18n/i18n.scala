package his.util

import java.util.{Locale, ResourceBundle}

import scala.util.Try

/**
  * Created by: 
  *
  * @author Raphael
  * @version 27.02.2018
  */
package object i18n {

  object LanguageManager {

    private val BUNDLE_NAME = "bundle.lang"

    private var bundleInst: ResourceBundle = _

    def loadForLocale(locale: Locale): Unit =
      bundleInst = ResourceBundle.getBundle(BUNDLE_NAME, locale)

    def bundle: ResourceBundle = bundleInst

    def isCurrentLangSupported: Boolean = Try(loadForLocale(Locale.getDefault)).isSuccess
  }

  implicit class I18NString(str: String) {
    def localize: String = Try(LanguageManager.bundle.getString(str)).fold(
      (_) => { System.err.println(s"ERROR: Unable to find: '$str' in Resource bundle!"); s"__${str}__" },
      (x) => x
    )
  }

}
