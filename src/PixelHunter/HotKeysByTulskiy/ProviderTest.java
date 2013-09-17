package PixelHunter.HotKeysByTulskiy;

import PixelHunter.HotKeysByTulskiy.Common.HotKey;
import PixelHunter.HotKeysByTulskiy.Common.HotKeyListener;
import PixelHunter.HotKeysByTulskiy.Common.Provider;

import javax.swing.*;

public class ProviderTest {
	public static void main(String[] args) {
		final Provider provider = Provider.getCurrentProvider(false);

		provider.register(KeyStroke.getKeyStroke("control alt D"), new HotKeyListener() {
			public void onHotKey(HotKey hotKey) {
				System.out.println(hotKey);
				provider.reset();
				provider.stop();
			}
		});

		HotKeyListener listener = new HotKeyListener() {
			public void onHotKey(HotKey hotKey) {
				System.out.println(hotKey);
			}
		};
		provider.register(KeyStroke.getKeyStroke("control shift 0"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift PLUS"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift INSERT"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift ESCAPE"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift BACK_QUOTE"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift SLASH"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift BACK_SLASH"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift DIVIDE"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift MULTIPLY"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift ENTER"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift MINUS"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift BACK_QUOTE"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift UP"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift INSERT"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift DELETE"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift ADD"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift SUBTRACT"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift COMMA"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift PERIOD"), listener);
		provider.register(KeyStroke.getKeyStroke("control shift SEMICOLON"), listener);
		provider.register(KeyStroke.getKeyStroke("control alt HOME"), listener);
		provider.register(KeyStroke.getKeyStroke("control alt PAGE_UP"), listener);
		provider.register(KeyStroke.getKeyStroke("control alt NUMPAD0"), listener);
		int i=0;
		while (true){
//			System.out.println("sleep "+i++);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}
}