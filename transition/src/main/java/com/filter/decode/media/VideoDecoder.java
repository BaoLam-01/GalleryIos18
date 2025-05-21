package com.filter.decode.media;

import com.filter.transition.model.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideoDecoder {

    private final int mAudioTrackIndex = MediaPlayer.TRACK_INDEX_AUTO;
    private final List<MediaPlayer> listMediaPlayer = new ArrayList<>();
    private MediaPlayer currentPlayer;
    private boolean isPreview = false;

    public VideoDecoder() {
    }

    private void setVideoSource(MediaSource source, int timeStart, int timeEnd, long id, boolean isMute) {
        openVideo(timeStart, timeEnd, id, source, isMute);
    }

    public void setSource(MediaSource source, int timeStart, int timeEnd, long id, boolean isMute) {
        setVideoSource(source, timeStart, timeEnd, id, isMute);
    }

    public void replaceSource(MediaSource mediaSource, int timeStart, int timeEnd, long id, int pos, boolean isImage) {
        replaceVideo(timeStart, timeEnd, id, pos, mediaSource, isImage);
    }

    public void replaceSource(MediaSource mediaSource, int timeStart, int timeEnd, long id, int pos, boolean isImage, boolean isMute) {
        replaceVideo(timeStart, timeEnd, id, pos, mediaSource, isImage, isMute);
    }


    public void addMediaPlayerWithPos(MediaSource source, int timeStart, Scene scene, int pos) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setIdMedia(scene.getId());
        if (scene.isImage()) {
            mediaPlayer.setTimeEnd(-1);
            mediaPlayer.setExistAudio(false);
            listMediaPlayer.add(pos, mediaPlayer);
        } else {
            mediaPlayer.setExistAudio(true);
            mediaPlayer.setScreenOnWhilePlaying(true);
            long hr = TimeUnit.MILLISECONDS.toHours(timeStart);
            long min = TimeUnit.MILLISECONDS.toMinutes(timeStart) % 60;
            long sec = TimeUnit.MILLISECONDS.toSeconds(timeStart) % 60;
            int timeS = (int) (hr * 3600 * 1000 + min * 60 * 1000 + sec * 1000);
            mediaPlayer.setTimeStart(timeS);
            mediaPlayer.setTimeEnd(scene.getDuration() + timeS);
            if (scene.getTimeStart() != 0) {
                mediaPlayer.setTimeStartTrim(timeStart);
            }
            listMediaPlayer.add(pos, mediaPlayer);
            startSource(source, mediaPlayer);
        }
    }

    public void addListMediaPlayer(List<MediaSource> sources, List<Scene> scenes, int pos) {
        List<MediaPlayer> list = new ArrayList<>();
        if (scenes.size() == sources.size()) {
            for (int i = 0; i < scenes.size(); i++) {
                Scene scene = scenes.get(i);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setIdMedia(scene.getId());
                if (scene.isImage()) {
                    mediaPlayer.setTimeEnd(-1);
                    mediaPlayer.setExistAudio(false);
                    list.add(mediaPlayer);
                } else {
                    mediaPlayer.setExistAudio(true);
                    mediaPlayer.setScreenOnWhilePlaying(true);
                    long hr = TimeUnit.MILLISECONDS.toHours(scene.getTimeStart());
                    long min = TimeUnit.MILLISECONDS.toMinutes(scene.getTimeStart()) % 60;
                    long sec = TimeUnit.MILLISECONDS.toSeconds(scene.getTimeStart()) % 60;
                    int timeS = (int) (hr * 3600 * 1000 + min * 60 * 1000 + sec * 1000);
                    mediaPlayer.setTimeStart(timeS);
                    mediaPlayer.setTimeEnd(scene.getDuration());
                    if (scene.getTimeStart() != 0) {
                        mediaPlayer.setTimeStartTrim(scene.getTimeStart());
                    }
                    list.add(mediaPlayer);
                    startSource(sources.get(i), mediaPlayer);
                }
            }
            listMediaPlayer.addAll(pos, list);
        }
    }

    public void addEmptyMediaPlayer(long id) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setIdMedia(id);
        mediaPlayer.setTimeEnd(-1);
        mediaPlayer.setExistAudio(false);
        listMediaPlayer.add(mediaPlayer);
    }

    private void replaceWithEmptyMediaPlayer(long id, int pos) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setIdMedia(id);
        mediaPlayer.setTimeEnd(-1);
        mediaPlayer.setExistAudio(false);
        listMediaPlayer.remove(pos);
        listMediaPlayer.add(pos, mediaPlayer);
    }

    private void openVideo(int timeStart, int timeEnd, long id, MediaSource source, boolean isMute) {
        if (source == null) {
            return;
        }
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        long hr = TimeUnit.MILLISECONDS.toHours(timeStart);
        long min = TimeUnit.MILLISECONDS.toMinutes(timeStart) % 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(timeStart) % 60;
        int timeS = (int) (hr * 3600 * 1000 + min * 60 * 1000 + sec * 1000);
        mPlayer.setTimeStart(timeS);
        mPlayer.setTimeEnd(timeEnd);
        mPlayer.setMute(isMute);
        if (timeStart != 0) {
            mPlayer.setTimeStartTrim(timeStart);
        }
        mPlayer.setIdMedia(id);
        listMediaPlayer.add(mPlayer);
        startSource(source, mPlayer);
    }

    private void replaceVideo(int timeStart, int timeEnd, long id, int pos, MediaSource source, boolean isImage) {
        if (source == null) {
            return;
        }
        if (isImage) {
            replaceWithEmptyMediaPlayer(id, pos);
            return;
        }
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        long hr = TimeUnit.MILLISECONDS.toHours(timeStart);
        long min = TimeUnit.MILLISECONDS.toMinutes(timeStart) % 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(timeStart) % 60;
        int timeS = (int) (hr * 3600 * 1000 + min * 60 * 1000 + sec * 1000);
        mPlayer.setTimeStart(timeS);
        mPlayer.setTimeEnd(timeEnd);
        mPlayer.setIdMedia(id);
        listMediaPlayer.remove(pos);
        listMediaPlayer.add(pos, mPlayer);
        startSource(source, mPlayer);
    }

    private void replaceVideo(int timeStart, int timeEnd, long id, int pos, MediaSource source, boolean isImage, boolean isMute) {
        if (source == null) {
            return;
        }
        if (isImage) {
            replaceWithEmptyMediaPlayer(id, pos);
            return;
        }
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setScreenOnWhilePlaying(true);
        long hr = TimeUnit.MILLISECONDS.toHours(timeStart);
        long min = TimeUnit.MILLISECONDS.toMinutes(timeStart) % 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(timeStart) % 60;
        int timeS = (int) (hr * 3600 * 1000 + min * 60 * 1000 + sec * 1000);
        mPlayer.setTimeStart(timeS);
        mPlayer.setTimeEnd(timeEnd);
        mPlayer.setIdMedia(id);
        mPlayer.setMute(isMute);
        if (timeStart != 0) {
            mPlayer.setTimeStartTrim(timeStart);
        }
        listMediaPlayer.remove(pos);
        listMediaPlayer.add(pos, mPlayer);
        startSource(source, mPlayer);
    }

    public void duplicateVideo(int pos, long id, MediaSource source, boolean isImage) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        MediaPlayer mPlayer = listMediaPlayer.get(pos);
        if (isImage) {
            mediaPlayer.setIdMedia(id);
            mediaPlayer.setTimeEnd(-1);
            mediaPlayer.setExistAudio(false);
        } else {
            mediaPlayer.setTimeStartTrim(mPlayer.getTimeStartTrim());
            mediaPlayer.setMute(mPlayer.isMute());
            long hr = TimeUnit.MILLISECONDS.toHours(mPlayer.getTimeStart());
            long min = TimeUnit.MILLISECONDS.toMinutes(mPlayer.getTimeStart()) % 60;
            long sec = TimeUnit.MILLISECONDS.toSeconds(mPlayer.getTimeStart()) % 60;
            int timeS = (int) (hr * 3600 * 1000 + min * 60 * 1000 + sec * 1000);
            mediaPlayer.setTimeStart(timeS);
            mediaPlayer.setTimeEnd(mPlayer.getTimeEnd());
            startSource(source, mediaPlayer);
        }
        listMediaPlayer.add(pos + 1, mediaPlayer);
    }

    public void deletePlayer(long id) {
        int size = listMediaPlayer.size();
        for (int i = 0; i < size; i++) {
            if (id == listMediaPlayer.get(i).getIdMedia()) {
                listMediaPlayer.get(i).release();
                listMediaPlayer.remove(i);
                break;
            }
        }
    }

    private void startSource(MediaSource source, MediaPlayer mPlayer) {
        new Thread(() -> {
            try {
                mPlayer.setDataSource(source, mAudioTrackIndex);
                mPlayer.prepareAsync();

            } catch (Exception e) {
                mPlayer.setExistAudio(false);
            }
        }).start();
    }

    public boolean isPlaying() {
        return currentPlayer != null && currentPlayer.isPlaying();
    }

    private boolean isStart = false;

    public void seekTo(int mSec) {
        if (isStart) {
            pause();
        }
        if (isInPlaybackState() && mSec >= currentPlayer.getTimeDelay() && mSec < currentPlayer.getTimeEnd()) {
            currentPlayer.seekTo(mSec - currentPlayer.getTimeDelay() + currentPlayer.getTimeStart());
        }
    }

    public void seekToAfterPreview(int time) {
        for (int i = 0; i < listMediaPlayer.size(); i++) {
            MediaPlayer mediaPlayer = listMediaPlayer.get(i);
            if (time >= mediaPlayer.getTimeDelay() && time < mediaPlayer.getTimeEnd()) {
                if (currentPlayer != mediaPlayer) {
                    currentPlayer = mediaPlayer;
                    break;
                }
            }
        }
        if (currentPlayer != null) {
            currentPlayer.seekTo(time - currentPlayer.getTimeDelay() + currentPlayer.getTimeStart());
        }
    }

    public void seekToStart() {
        for (int i = 0; i < listMediaPlayer.size(); i++) {
            listMediaPlayer.get(i).seekToStart();
        }
    }

    private boolean isInPlaybackState() {
        return currentPlayer != null;
    }

    public void start(int time) {
        setCurrentPlayerNoStart(time);
        if (isInPlaybackState()) {
            if (isPreview) {
                int timeP = time - currentPlayer.getTimeDelay() + currentPlayer.getTimeStart();
                if (timeP > 0) currentPlayer.seekTo(timeP);
                isPreview = false;
            }
            currentPlayer.start();
            isStart = true;
        }
    }

    private void start() {
        if (currentPlayer != null) {
            currentPlayer.seekTo(currentPlayer.getTimeStart());
        }
        if (isInPlaybackState()) {
            currentPlayer.start();
            isStart = true;
        }
    }

    public void pause() {
        if (isInPlaybackState()) {
            currentPlayer.pause();
            isStart = false;
        }
        currentPlayer = null;
    }

    public void release() {
        for (int i = 0; i < listMediaPlayer.size(); i++) {
            if (listMediaPlayer.get(i) != null) {
                listMediaPlayer.get(i).release();
            }
        }
        listMediaPlayer.clear();
    }

    public void setCurrentPlayer(int pos, boolean isPlay) {
        if (listMediaPlayer.isEmpty()) {
            return;
        }
        if (pos >= listMediaPlayer.size()) {
            pos = listMediaPlayer.size() - 1;
        }
        if (currentPlayer != listMediaPlayer.get(pos)) {
            pause();
            currentPlayer = listMediaPlayer.get(pos);
            if (isPlay) {
                start();
            }
        }
    }

    public void setCurrentPlayerNoStart(int time) {
        int size = listMediaPlayer.size();
        for (int i = 0; i < size; i++) {
            if (listMediaPlayer.get(i).getTimeDelay() <= time && listMediaPlayer.get(i).getTimeEnd() > time) {
                if (currentPlayer != listMediaPlayer.get(i)) {
                    currentPlayer = listMediaPlayer.get(i);
                    currentPlayer.seekTo(time - currentPlayer.getTimeDelay() + currentPlayer.getTimeStart());
                    break;
                }
            }
        }
    }

    public void changeDurationMediaPlayer(int timeStart, int timeEnd, int pos, boolean isTrim) {
        if (pos >= listMediaPlayer.size()) {
            return;
        }
        listMediaPlayer.get(pos).setTimeStart(timeStart);
        listMediaPlayer.get(pos).setTimeEnd(timeEnd);
        if (isTrim) {
            listMediaPlayer.get(pos).setTimeStartTrim(timeStart);
        }
        listMediaPlayer.get(pos).setTimeToDecoder();
    }

    public void setTimeDelayToMediaPlayer(List<Scene> list) {
        if (list.isEmpty()) {
            return;
        }
        int temp = 0;
        for (int i = 0; i < list.size(); i++) {
            listMediaPlayer.get(i).setTimeDelay(temp);
            temp += list.get(i).calVisibleDuration();
        }
        Collections.sort(listMediaPlayer, (mediaPlayer, t1) -> Integer.compare(mediaPlayer.getTimeDelay(), t1.getTimeDelay()));
    }

    public void muteAllPlayer(boolean isMuteAll) {
        for (MediaPlayer mediaPlayer : listMediaPlayer) {
            mediaPlayer.setMuteAll(isMuteAll);
        }
    }


    public void mutePlayer(int pos) {
        listMediaPlayer.get(pos).setMute(true);
    }

    public void mutePlayerById(long id, boolean isMute) {
        for (MediaPlayer mediaPlayer : listMediaPlayer) {
            if (id == mediaPlayer.getIdMedia()) {
                mediaPlayer.setMute(isMute);
            }
        }
    }

    public void unMutePlayer(int pos) {
        listMediaPlayer.get(pos).setMute(false);
    }

    public boolean isMute(int pos) {
        return !listMediaPlayer.get(pos).isMute();
    }

    public boolean isExistAudio(int pos) {
        return listMediaPlayer.get(pos).isExistAudio();
    }

    public boolean getIsMuteById(long id) {
        for (MediaPlayer mediaPlayer : listMediaPlayer) {
            if (id == mediaPlayer.getIdMedia()) {
                return mediaPlayer.isMute();
            }
        }
        return true;
    }

    public int getTimeDelayByPos(int pos) {
        return listMediaPlayer.get(pos).getTimeDelay();
    }

    public int getTimeStartByPos(int pos) {
        return listMediaPlayer.get(pos).getTimeStart();
    }

    public int getDuration(int pos) {
        return listMediaPlayer.get(pos).getDuration();
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }
}
