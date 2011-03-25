package com.alu.alice_game;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GameReceiver extends BroadcastReceiver{

	private static final String CALL_START_ACTION = "com.alu.game.Main.CALL_STARTED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(CALL_START_ACTION)){
			Intent i = new Intent();
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("from_mwc", 0);
			i.putExtra("first_player", "Patrick");
			i.putExtra("second_player", "Laia");
			//i.putExtra("first_number", "9059680129");
			//i.putExtra("second_number", "callNumber");
			i.setComponent(new ComponentName("com.alu.alice_game", "com.alu.alice_game.Main"));
			context.startActivity(i);
			Log.i("GameReceiver","Bring Game to the Front");
		}
	}

}
