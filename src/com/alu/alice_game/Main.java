package com.alu.alice_game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.alu.alice_game.server.MultiPlayerAwsSupportImpl;
import com.alu.alice_game.server.MultiPlayerStubSupportImpl;
import com.alu.alice_game.server.MultiPlayerSupport;
import com.alu.alice_game.domain.Player;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends Activity {
	
	//private static int IMAGE_POSITION_TAG = 0;
	
	private ArrayList<Integer> image_array = new ArrayList<Integer>();
	
	private ArrayList<Integer> random_image_array = new ArrayList<Integer>();
	private ArrayList<Integer> print_image_array = new ArrayList<Integer>();
	private ArrayList<Integer> send_image_array = new ArrayList<Integer>();
	
	// sender sets the order
	private boolean isSender = true; 
	
	//receiver tries to playback the order
	private boolean isReceiver = true;
	
	private Handler mHandler = new Handler();
	
	
	private Button sound_optn;
	private ImageButton button0;
	private ImageButton button1;
	private ImageButton button2;
	private ImageButton playAgainBtn;
	private ImageButton tryAgainBtn;
	private ImageView image_0;
	private ImageView image_1;
	private ImageView image_2;
	private int button0_image = R.drawable.cartoon0;
	private int button1_image = R.drawable.cartoon1;
	private int button2_image = R.drawable.cartoon2;
	private int inactive_image = R.drawable.cartoon3;
	
	private MediaPlayer background_music;
	
	private MultiPlayerSupport multiPlayerSupport;
	private Collection<Player> inGamePlayers;
	private Player player1;
	private Player player2;
	private Player sendPlayer;
	private Player receivePlayer;
	
	public Main() {
		multiPlayerSupport = new MultiPlayerStubSupportImpl();
		// multiPlayerSupport = new MultiPlayerAwsSupportImpl();
	}
	
	// Used to make a timer
	private class DummyRunnable implements Runnable{
		Context cxt;
		public DummyRunnable(Context cxt){
			this.cxt = 	cxt; 
		}
		@Override
		public void run() {
			Main m = (Main) cxt;
		}
		
	}
	
	private Runnable TimeDelay = new DummyRunnable(this) {
		public void run(){
			
		}
	};
	
    /** Called when the activity is first created. */
	private int numOfRounds = 3;

	private ArrayList<Integer> genOrder(){
		ArrayList<Integer> random_image_array = new ArrayList<Integer>();
		Random r = new Random();
		Log.i("genOrder()", "before while");
		while(image_array.size() > 0){
			int rand_num = r.nextInt(image_array.size());
			random_image_array.add( image_array.get(rand_num));
			image_array.remove(rand_num);
			Log.i("Main","Random number is " + rand_num);
		}
		return random_image_array;
	}//genOrder
	
	private void startSendAnimation(){
		ImageView image = (ImageView) findViewById(send_image_array.get(0));
		send_image_array.remove(0);
		Animation move = AnimationUtils.loadAnimation(Main.this, R.anim.z_move_1);
		move.setAnimationListener(new sendAnimListener());
		image.startAnimation(move);
		
		Log.i("Main","startSendAnimation()");
	}
	
	private class sendAnimListener implements Animation.AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			if(send_image_array.size() > 0){
				Main.this.startSendAnimation();
			}else {
				Toast greeting_instructions = Toast.makeText(getApplicationContext(), "Set the Order", Toast.LENGTH_SHORT);
		        greeting_instructions.setGravity(Gravity.CENTER, 0, -50);
		        greeting_instructions.show();
				isSender = false;
				Main.this.retrieve_image_array();
			Log.i("sendStartAnimation", "onAnimationEnd");
			}
			
		}//onAnimationEnd

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
	}//sendAnimListener
	
	// starts the animation of three characters when receiving and destroys print_image_array
	private void startAnimation(){
		//ArrayList<Integer> image_array_copy = random_image_array;
		ImageView image = (ImageView) findViewById(print_image_array.get(0));
		print_image_array.remove(0);
		Animation move = AnimationUtils.loadAnimation(Main.this, R.anim.z_move);
		move.setAnimationListener(new AnimListener());
		image.startAnimation(move);
		
		Log.i("Main","onAnimtionEnd");
	}// startAnimation
	
	
	//Checks for End of animation and starts the next animation.
	private class AnimListener implements Animation.AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if( print_image_array.size() > 0 ){
			Main.this.startAnimation();
			}
			else{

				button0.setVisibility(View.VISIBLE);
				button1.setVisibility(View.VISIBLE);
				button2.setVisibility(View.VISIBLE);
				 // Instructions for Game
		        Toast greeting_instructions = Toast.makeText(getApplicationContext(), "Click In the Same Order", Toast.LENGTH_SHORT);
		        greeting_instructions.setGravity(Gravity.CENTER, 0, -50);
		        greeting_instructions.show();
		       
			}
			Log.i("Main", "onAnimationEnd()");
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
	}// AnimListener
	
	//resets the buttons state to active
	private void reset_button(){
		button0.setClickable(true);
		button1.setClickable(true);
		button2.setClickable(true);
		button0.setImageResource(button0_image);
		button1.setImageResource(button1_image);
		button2.setImageResource(button2_image);
		button0.setVisibility(View.INVISIBLE);
		button1.setVisibility(View.INVISIBLE);
		button2.setVisibility(View.INVISIBLE);
	}// reset_buttons
	
	//resets the game
	private void reset(){
		//score = 0;
		//myScore.setText("Score:" + score);
		player1.resetScore();
		player2.resetScore();
		numOfRounds =3;
		random_image_array.clear();
		this.reset_button();
		this.gameSetUpPerRound();
	}//reset
	
	
	// Use to start each round of the game and creates a random order 
	private void gameSetUpPerRound(){
		// Win Game is rounds == 0
		if(numOfRounds == 0){
			//announce win screen
			Toast toast = Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, -50);
			toast.show();
			// launch play again window
			Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
	        fade_in.setAnimationListener(new Animation.AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					playAgainBtn.setClickable(false);
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					playAgainBtn.setVisibility(View.VISIBLE);
					playAgainBtn.setClickable(true);
					
				}
			});
	        playAgainBtn.startAnimation(fade_in);
	        background_music.stop();
	        
			Log.i("gameSetUpPerRound", "Win");
		}// if
		else{
			image_array.add( new Integer(R.id.image0));
	    	image_array.add( new Integer(R.id.image1));
	        image_array.add( new Integer(R.id.image2));
	        random_image_array = this.genOrder();
	        for(int i = 0; i < random_image_array.size(); i++){
	        	print_image_array.add(random_image_array.get(i));
	        }// for
	        this.startAnimation();
	        if(numOfRounds == 3){
	        Toast round = Toast.makeText(getApplicationContext(), "Starting Round 1", Toast.LENGTH_SHORT);
	        round.setGravity(Gravity.CENTER, 0, -50);
	        round.show();
	        }// if
	        numOfRounds--;
		}// else
		Log.i("Main", "gameSetUpPerRound");
	}// gameSetUpPerRound
	
	
	// Used to determine whether the clicks are right or wrong
	private class myOnClick implements View.OnClickListener {

		Main m;
		ImageButton button;
		
		public myOnClick(Main m, ImageButton button){
			this.m = m;
			this.button = button;
		}

		@Override
		public void onClick(View v) {
			Integer image_position_pressed = (Integer) v.getTag(R.string.image_position_tag);
			if(isSender){
				//disable button
				button.setClickable(false);
				button.setImageResource(inactive_image);
				//Set the image into array
				random_image_array.add(image_position_pressed);
				send_image_array.add(image_position_pressed);
				print_image_array.add(image_position_pressed);
				if(send_image_array.size() == 3){
					m.sendSequence();
				}
			}else{ // isReceiver
				// Below is Code for player to follow sequence
			Integer image_position_current = random_image_array.get(0);
			if(image_position_pressed.equals(image_position_current)) {
				//disable button
				button.setClickable(false);
				button.setImageResource(inactive_image);
				//increase score
				receivePlayer.addScore();
				receivePlayer.updateScoreboard();
				//myScore.setText("Score:" + score);
				random_image_array.remove(0);
				Log.i("Main", "right");
				if(random_image_array.size() == 0){
					//end current round
					String continue_msg ="\nStarting Round " + (4 - numOfRounds);
					if(numOfRounds == 0){
						continue_msg = "";
					}
					Toast nextround = Toast.makeText(getApplicationContext(), "Round Complete" + continue_msg, Toast.LENGTH_SHORT);
					nextround.setGravity(Gravity.CENTER, 0, -50);
					nextround.show();
					//update score
					receivePlayer.updateScoreboard();
					//myScore.setText("Score:" + score);
					for(int i = 0; i < 1000000 ; i++);
					m.isSender = true;
					// start next round
					m.reset_button();
					m.startRound();
					
				}//if
			} else {
				// display new scores
				//Toast toast = Toast.makeText(getApplicationContext(), "Game Over\nScore:" + score, Toast.LENGTH_LONG);
				//toast.setGravity(Gravity.CENTER, 0, -50);
				//toast.show();
				// display 
				m.reset_button();
				Log.i("Main", "wrong");
			}//else correct press
			}//else (isSender)
			
		}
		
	}// myOnClick
	/*
	// Used to determine whether the clicks are right or wrong
	private class myOnClick implements View.OnClickListener {

		Main m;
		ImageButton button;
		
		public myOnClick(Main m, ImageButton button){
			this.m = m;
			this.button = button;
		}

		@Override
		public void onClick(View v) {
			Integer image_position_pressed = (Integer) v.getTag(R.string.image_position_tag);
			// Below is Code for player to follow sequence
			Integer image_position_current = random_image_array.get(0);
			if(image_position_pressed.equals(image_position_current)) {
				//disable button
				button.setClickable(false);
				button.setImageResource(inactive_image);
				//increase score
				score ++;
				myScore.setText("Score:" + score);
				random_image_array.remove(0);
				Log.i("Main", "right");
				if(random_image_array.size() == 0){
					//end current round
					String continue_msg ="\nStarting Round " + (4 - numOfRounds);
					if(numOfRounds == 0){
						continue_msg = "";
					}
					Toast nextround = Toast.makeText(getApplicationContext(), "Round Complete" + continue_msg, Toast.LENGTH_SHORT);
					nextround.setGravity(Gravity.CENTER, 0, -50);
					nextround.show();
					//update score
					myScore.setText("Score:" + score);
					for(int i = 0; i < 1000000 ; i++);
					// start next round
					m.gameSetUpPerRound();
					m.reset_button();
				}//if
			} else {
				// game over screen
				Toast toast = Toast.makeText(getApplicationContext(), "Game Over\nScore:" + score, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, -50);
				toast.show();
				// launch try again
				m.reset_button();
				Animation fade_in = AnimationUtils.loadAnimation(m, R.anim.fade_in);
				fade_in.setAnimationListener(new Animation.AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						tryAgainBtn.setClickable(false);
						
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						tryAgainBtn.setVisibility(View.VISIBLE);
						tryAgainBtn.setClickable(true);
					}
				});
				tryAgainBtn.startAnimation(fade_in);
				Log.i("Main", "wrong");
			}
			
		}
		
	}// myOnClick
	
	
	
	*/
	
	//To Reset the game when lost or win.
	private class myPlayAgainClickListener implements View.OnClickListener{
		Main m;
		myPlayAgainClickListener(Main m){
			this.m = m;
		}
		
		@Override
		public void onClick(View v) {
			playAgainBtn.setVisibility(View.INVISIBLE);
			tryAgainBtn.setVisibility(View.INVISIBLE);
			playAgainBtn.setClickable(false);
			tryAgainBtn.setClickable(false);
			m.reset();
		}
		
	}// myPlayAgainClickListener
	
	
	private void sendSequence(){
		//Random r = new Random();
		Log.i("sendSequence()", "before randomizer");
		/*
		//Random Setting Number
		while(image_array.size() > 0){
			int rand_num = r.nextInt(image_array.size());
			random_image_array.add( image_array.get(rand_num));
			image_array.remove(rand_num);
			Log.i("setSeqeunce()","Random number is " + rand_num);
		}
		*/
		this.reset_button();

		this.startSendAnimation();
		// Send random_image_array to Receiver's queue
		
	}//sendSequence
	
	private void retrieve_image_array(){
		if(print_image_array.size() != 3){
			//try to get message from server
			Log.i("retrieve_image_array()", "Waiting for Retrieval");
		}else{
		this.startAnimation();
		}
	}
	
	private void startRound(){
		if(isSender){
			image_array.add( new Integer(R.id.image0));
	    	image_array.add( new Integer(R.id.image1));
	        image_array.add( new Integer(R.id.image2));
	        button0.setVisibility(View.VISIBLE);
			button1.setVisibility(View.VISIBLE);
			button2.setVisibility(View.VISIBLE);
			
		}else{
			this.retrieve_image_array();
		}//else
	
	}//startRound
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // create base set of image id
        
        setContentView(R.layout.main);
        //Initialization
        
        //Used to delay Actions for testing
        mHandler.removeCallbacks(TimeDelay);
        mHandler.postDelayed(TimeDelay, 5000);
        
        //UI Items
        player1 = new Player();
        player1.setName("Ted");
        player1.scoreboard = (TextView) findViewById(R.id.score_text1);
        player1.updateScoreboard();
        //p1Score.setText(Player1.getName() + ": " + Player1.getScore());
        player2 = new Player();
        player2.setName("Barney");
        player2.scoreboard = (TextView) findViewById(R.id.score_text2);
        player2.updateScoreboard();
        //p2Score.setText(Player2.getName() + ": " + Player2.getScore());
        image_0 = (ImageView) findViewById(R.id.image0);
        image_1 = (ImageView) findViewById(R.id.image1);
        image_2 = (ImageView) findViewById(R.id.image2);
        sound_optn = (Button) findViewById (R.id.sound_optn);
        button0 = (ImageButton) findViewById(R.id.button0);
        button1 = (ImageButton) findViewById(R.id.button1);
        button2 = (ImageButton) findViewById(R.id.button2);
        playAgainBtn = (ImageButton) findViewById(R.id.play_again_img);
        playAgainBtn.setClickable(false);
        tryAgainBtn = (ImageButton) findViewById(R.id.try_again_img);
        tryAgainBtn.setClickable(false);
        // hide images
        image_0.setVisibility(View.INVISIBLE);
        image_1.setVisibility(View.INVISIBLE);
        image_2.setVisibility(View.INVISIBLE);
        // hide buttons
		button0.setVisibility(View.INVISIBLE);
		button1.setVisibility(View.INVISIBLE);
		button2.setVisibility(View.INVISIBLE);

		
		background_music = MediaPlayer.create(getApplicationContext(), R.raw.freeze_ray);
		background_music.setLooping(true);
		background_music.start();
        
		//starts game here
		this.startRound();
		//this.gameSetUpPerRound();
        
        playAgainBtn.setOnClickListener(new myPlayAgainClickListener(this));
        tryAgainBtn.setOnClickListener(new myPlayAgainClickListener(this));
	   
        //Sound Options
        
        sound_optn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Check Sound Options
				try{
					if(background_music.isPlaying()){
						background_music.pause();
						//unmute image
					}//if
					else{
						background_music.start();
						//mute image
					}//else
				} catch(IllegalStateException e){
					
				}//catch
			}
		});
        
        // 3 Image Buttons
		button0.setTag(R.string.image_position_tag, new Integer(R.id.image0));
		button0.setOnClickListener(new myOnClick(this, button0));
		    	      	   
		    	   /*
		    	   button0.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Integer image = R.id.image0;
						if( image.equals(random_image_array.get(0))){
							// disable button
							button0.setClickable(false);
							
							// increase score
							score++;
							myScore.setText("Score: " + score);
							//remove element
							random_image_array.remove(0);
							Log.i("Button0", "Right");
						}//if
						else{
							score--;
							myScore.setText("Score:" + score);
							//Lose Animation
							//display Try Again Text
							//random_image_array.clear();
							Log.i("Button0", "Wrong");
						}//else
						
					}//onClick
		    	   });// setOnClickListener
		    	   */
		    	   
		button1.setTag(R.string.image_position_tag, new Integer(R.id.image1));
		button1.setOnClickListener(new myOnClick(this, button1));
		    	  
		button2.setTag(R.string.image_position_tag, new Integer(R.id.image2));
		button2.setOnClickListener(new myOnClick(this, button2));
		    	
		    	
        Log.i("Main", "onCreate");
    }// onCreate
    
    
    public void onStop(){
    	//Stops background music if it is playing.
    	try{
    	if(background_music.isPlaying()){
    	background_music.stop();
    	}//if
    	}catch (IllegalStateException e){
    	}
    	// must call otherwise will cause errors
    	super.onStop();
    }//onStop
}//Main