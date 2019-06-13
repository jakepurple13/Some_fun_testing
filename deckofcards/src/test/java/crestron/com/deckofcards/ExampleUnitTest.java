package crestron.com.deckofcards;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    private void log(String s) {
        System.out.println(s);
    }

    @Test
    public void deckTest() throws CardNotFoundException {
        Deck d = new Deck(false);

        System.out.println(d.removeColor(Color.BLACK));

        System.out.println(d.removeSuit(Suit.HEARTS));

        System.out.println(d.removeNumber(6));

        System.out.println(d);

        d = new Deck(false);

        log(d.getRandomCard().toString());
        log(d.getCard(4).toString());

        /*Deck d = new Deck(true, 1);
		Deck q = new Deck(true, 2);

		for(int i=0;i<d.getSize();i++) {
			System.out.println(d.draw() + "\t" + q.draw());
		}

		Deck j = new Deck(false);
		j.shuffle(1L);
		//for(int i=0;i<d.getSize();i++) {
			//System.out.println(j.draw());
		//}

		Deck t = new Deck(true);
		//t.shuffle(1);
		*//*for(int i=0;i<d.deckCount();i++) {
			Card c = t.draw();
			//System.out.println(c + "\t" + c.getImage().toString());
		}
		*//*

		Hand h = new Hand("Jacob");
		t.dealHand(h, 5);

		System.out.println(h);
		h.sortHandByValue();
		System.out.println(h.toString());
		h.sortHandBySuit();
		System.out.println(h.toString());
		h.clearHand();
		System.out.println(h.toString());*/

    }
}