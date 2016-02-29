package net.web.base.captcha;

import java.awt.Color;
import java.awt.Font;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class CaptchaEngine extends ListImageCaptchaEngine {

	private static final int IMAGE_WIDTH = 80;
	private static final int IMAGE_HEIGHT = 28;

	private static int MIN_WORD_LENGTH = 4;
	private static int MAX_WORD_LENGTH = 4;

	private static int MIN_FONT_SIZE = 12;
	private static int MAX_FONT_SIZE = 16;

	private static final String CHAR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZSZHZOZPZXZXZ";

	private static final Font[] FONTS = new Font[] { new Font("nyala", Font.BOLD, MAX_FONT_SIZE), new Font("Arial", Font.BOLD, MAX_FONT_SIZE), new Font("nyala", Font.BOLD, MAX_FONT_SIZE), new Font("Bell", Font.BOLD, MAX_FONT_SIZE), new Font("Bell MT", Font.BOLD, MAX_FONT_SIZE), new Font("Credit", Font.BOLD, MAX_FONT_SIZE), new Font("valley", Font.BOLD, MAX_FONT_SIZE), new Font("Impact", Font.BOLD, MAX_FONT_SIZE) };

	@Override
	protected void buildInitialFactories() {
		WordGenerator wordGenerator = new RandomWordGenerator(CHAR_STRING);
		TextPaster randomPaster = new DecoratedRandomTextPaster(MIN_WORD_LENGTH, MAX_WORD_LENGTH, new RandomListColorGenerator(new Color[] { new Color(23, 170, 27), new Color(220, 34, 11), new Color(23, 67, 172) }), new TextDecorator[] {});
		BackgroundGenerator background = new UniColorBackgroundGenerator(IMAGE_WIDTH, IMAGE_HEIGHT, Color.WHITE);
		FontGenerator font = new RandomFontGenerator(MIN_FONT_SIZE, MAX_FONT_SIZE, FONTS);
		WordToImage word2image = new ComposedWordToImage(font, background, randomPaster);
		addFactory(new GimpyFactory(wordGenerator, word2image));
	}

}