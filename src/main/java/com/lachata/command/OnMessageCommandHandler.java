package com.lachata.command;

import com.lachata.config.BotSetting;
import com.lachata.entity.MusicQueue;
import com.lachata.manager.LavaMusicManager;
import com.lachata.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class OnMessageCommandHandler implements GeneralBotCommand{
//	private static final List<String> commandDictionary = List.of("재생", "정지", "스킵", "대기열", "현재", "볼륨");
	private final EmbedUtils embedUtils;

	public OnMessageCommandHandler(EmbedUtils embedUtils) {
		this.embedUtils = embedUtils;
	}

	public void handleCommand(MessageReceivedEvent mre, String commandName, String arguments) {
		TextChannel channel = mre.getChannel().asTextChannel();
		Guild guild = mre.getGuild();

		switch(commandName){
			case "재생":
				inviteBotVoice(mre);
				LavaMusicManager.loadAndPlay(channel , guild, arguments);
				break;
			case "일시정지":
				if(!LavaMusicManager.isTrackPaused(guild)) {
					LavaMusicManager.pauseTrack(channel, guild);
					break;
				}

			case "재개":
				if(LavaMusicManager.isTrackPaused(guild)) {
					LavaMusicManager.resumeTrack(channel, guild);
					break;
				}

			case "스킵":
				LavaMusicManager.skipTrack(guild);
				break;

			case "대기열":
				MusicQueue nowPlayinLinst = LavaMusicManager.nowQueueList(guild);
				EmbedBuilder builder = embedUtils.createQueueEmbed(nowPlayinLinst);

				channel.sendMessageEmbeds(builder.build()).queue();
				break;

			case "현재":
				LavaMusicManager.get
				break;
			case "볼륨":
				int wannaVolume = Integer.parseInt(arguments);
				LavaMusicManager.setVolume(channel, guild, wannaVolume);
		}
	}
	@Override
	public void playingMusic() {

	}

	@Override
	public void musicStop() {

	}

	@Override
	public void musicSkip() {

	}

	@Override
	public MusicQueue nowQueue() {
		return null;
	}


	private void inviteBotVoice(MessageReceivedEvent mre) {
		if (mre.getMember() == null || mre.getMember().getVoiceState() == null) {
			// 사용자가 음성 채널에 참여하지 않으면 메시지를 출력
			mre.getChannel().sendMessage("음성 채널에 참여한 후 명령어를 사용해 주세요.").queue();
			return;
		}

		final AudioChannelUnion audioChannel = mre.getMember().getVoiceState().getChannel();

		if (audioChannel != null && audioChannel.getType().isAudio()) {
			// AudioChannelUnion을 VoiceChannel로 변환
			VoiceChannel voiceChannel = audioChannel.asVoiceChannel();

			// 봇을 해당 음성 채널에 연결
			Guild guild = mre.getGuild();
			guild.getAudioManager().openAudioConnection(voiceChannel);
		} else {
			mre.getChannel().sendMessage("음성 채널에 참여한 후 명령어를 사용해 주세요.").queue();
		}
	}

	public void handleChannelSetting(String message, BotSetting botSetting, MessageChannelUnion channel) {
		if (message.substring(3).startsWith("설정"))
			addChannel(botSetting, channel);
		if(message.substring(3).startsWith("보기"))
		    viewChannel(botSetting, channel);
	}


	private void addChannel(BotSetting botSetting, MessageChannelUnion messageChannelUnion) {
		Channel ch = messageChannelUnion.asTextChannel();
		botSetting.setChannelList(ch);
		messageChannelUnion.sendMessage("입력하신 채널 설정이 완료되었습니다.").queue();
	}

	private void viewChannel(BotSetting botSetting, MessageChannelUnion messageChannelUnion) {
		messageChannelUnion.sendMessage("설정된 채널 : "+ botSetting.toStringChannelList()).queue();
	}
}
