package io.github.subhamtyagi.ocr.utils;

/**
 * Various constant: Self explanatory
 */
public class Constants {


    /***
     *TRAINING DATA URL TEMPLATES for downloading
     */
    public static final String TESSERACT_DATA_DOWNLOAD_URL_BEST = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata_best/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_STANDARD = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_FAST = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata_fast/raw/4.0.0/%s.traineddata";

    public static final String TESSERACT_DATA_DOWNLOAD_URL_EQU = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata/raw/3.04.00/equ.traineddata";

    public static final String TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata_contrib/raw/main/akk/best/akk.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_AKK_STANDARD = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata_contrib/raw/main/akk/legacy/akk.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_AKK_FAST = "https://ghfast.top/https://github.com/tesseract-ocr/tessdata_contrib/raw/main/akk/fast/akk.traineddata";

    public static final String LANGUAGE_CODE = "%s.traineddata";

    public static final String KEY_TESS_TRAINING_DATA_SOURCE = "tess_training_data_source";
    public static final String KEY_GRAYSCALE_IMAGE_OCR = "grayscale_image_ocr";
    public static final String KEY_LAST_USE_IMAGE_LOCATION = "last_use_image_location";
    public static final String KEY_LAST_USE_IMAGE_TEXT = "last_use_image_text";
    public static final String KEY_PERSIST_DATA = "persist_data";

    public static final String KEY_LAST_USED_LANGUAGE_1 = "key_language_1";
    public static final String KEY_LAST_USED_LANGUAGE_2 = "key_language_2";
    public static final String KEY_LAST_USED_LANGUAGE_3 = "key_language_3";

    public static final String KEY_CONTRAST = "process_contrast";
    public static final String KEY_UN_SHARP_MASKING = "un_sharp_mask";
    public static final String KEY_OTSU_THRESHOLD = "otsu_threshold";
    public static final String KEY_FIND_SKEW_AND_DESKEW = "deskew_img";
    public static final String KEY_PAGE_SEG_MODE = "key_ocr_psm_mode";
    public static final String KEY_OCR_PSM_MODE = "key_ocr_psm_mode";
    public static final String KEY_ADVANCE_TESS_OPTION = "key_advance_tess_option";


}
