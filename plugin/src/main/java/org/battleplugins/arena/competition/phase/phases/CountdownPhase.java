package org.battleplugins.arena.competition.phase.phases;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.phase.LiveCompetitionPhase;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.util.UnitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class CountdownPhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {

    @ArenaOption(name = "revert-phase", description = "Whether the phase should revert if there are not enough players to start.")
    private boolean revertPhase = true;

    @ArenaOption(name = "countdown-time", description = "The time to countdown for the competition to start.", required = true)
    private int countdownTime;

    private int countdown;
    private BukkitTask countdownTask;

    @Override
    public void onStart() {
        this.countdown = this.countdownTime;
        this.countdownTask = Bukkit.getScheduler().runTaskTimer(this.competition.getArena().getPlugin(), () -> {
            if (this.countdown == 0) {
                this.advanceToNextPhase();
                return;
            }

            this.onCountdown();
            this.countdown--;
        }, 0L, 20L);
    }

    @ArenaEventHandler
    public void onLeave(ArenaLeaveEvent event) {
        if (!this.revertPhase || !(this.previousPhase instanceof WaitingPhase<T> waitingPhase)) {
            return;
        }

        if (!waitingPhase.hasEnoughPlayersToStart()) {
            this.countdownTask.cancel();

            this.setPhase(this.previousPhase.getType(), false);
            for (ArenaPlayer player : this.competition.getPlayers()) {
                Messages.ARENA_START_CANCELLED.send(player.getPlayer());
            }
        }
    }

    private void onCountdown() {
        if (this.countdown == 30 || this.countdown == 15 || this.countdown == 10 || this.countdown <= 5) {
            for (ArenaPlayer arenaPlayer : this.getCompetition().getPlayers()) {
                Player player = arenaPlayer.getPlayer();
                String timeToStart = UnitUtil.toUnitString(player, this.countdown, TimeUnit.SECONDS);

                Messages.ARENA_STARTS_IN.send(player, this.competition.getArena().getName(), timeToStart);
            }
        }
    }

    @Override
    public void onComplete() {
        this.countdownTask.cancel();
    }
}
