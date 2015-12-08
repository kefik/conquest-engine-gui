// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package conquest.engine.robot;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import conquest.engine.Robot;
import conquest.engine.io.handler.Handler;
import conquest.game.RegionData;
import conquest.game.move.Move;


public class IORobot implements Robot
{
	Handler handler;
	
	StringBuilder dump;

	int errorCounter;
	
	final int maxErrors = 2;
	
	public IORobot(String playerName, OutputStream input, boolean inputAutoFlush, InputStream output, InputStream error) throws IOException
	{
		handler = new Handler(playerName + "-Robot", input, inputAutoFlush, output, error);
		dump = new StringBuilder();
		errorCounter = 0;
	}
	
	@Override
	public void setup(long timeOut)
	{
	}
	
	@Override
	public void writeMove(Move move) {
	}
	
	@Override
	public String getPreferredStartingArmies(long timeOut, ArrayList<RegionData> pickableRegions)
	{
		String output = "pick_starting_regions " + timeOut;
		for(RegionData region : pickableRegions)
			output = output.concat(" " + region.getId());
		
		handler.writeLine(output);
		String line = handler.readLine(timeOut);
		dump.append(output + "\n");
		dump.append(line + "\n");
		return line;
	}
	
	@Override
	public String getPlaceArmiesMoves(long timeOut)
	{
		return getMoves("place_armies", timeOut);
	}
	
	@Override
	public String getAttackTransferMoves(long timeOut)
	{
		return getMoves("attack/transfer", timeOut);
	}
	
	private String getMoves(String moveType, long timeOut)
	{
		String line = "";
		if(errorCounter < maxErrors)
		{
			handler.writeLine("go " + moveType + " " + timeOut);
			dump.append("go " + moveType + " " + timeOut + "\n");
			
			
			long timeStart = System.currentTimeMillis();
			while(line != null && line.length() < 1)
			{
				long timeNow = System.currentTimeMillis();
				long timeElapsed = timeNow - timeStart;
				line = handler.readLine(timeOut);
				dump.append(line + "\n");
				if(timeElapsed >= timeOut)
					break;
			}
			if(line == null) {
				errorCounter++;
				return "";
			}
			if(line.equals("No moves"))
				return "";
		}
		else
		{
			dump.append("go " + moveType + " " + timeOut + "\n");
			dump.append("Maximum number of idle moves returned: skipping move (let bot return 'No moves' instead of nothing)");
		}
		return line;
	}
	
	@Override
	public void writeInfo(String info){
		handler.writeLine(info);
		dump.append(info + "\n");
	}

	public void addToDump(String dumpy){
		dump.append(dumpy);
	}
	
	public boolean isRunning() {
		return handler.isRunning();
	}
	
	public void finish() {
		handler.stop();
	}
	
	public String getIn() {
		return handler.getIn();
	}
	
	public String getOut() {
		return handler.getOut();
	}
	
	public String getErr() {
		return handler.getErr();
	}

	public String getDump() {
		return dump.toString();
	}

}