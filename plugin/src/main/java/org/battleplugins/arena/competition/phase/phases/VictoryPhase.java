package org.battleplugins.arena.competition.phase.phases;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.phase.LiveCompetitionPhase;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.event.arena.ArenaDrawEvent;
import org.battleplugins.arena.event.arena.ArenaLoseEvent;
import org.battleplugins.arena.event.arena.ArenaVictoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class VictoryPhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {
    @ArenaOption(name = "duration", description = "The number of seconds to remain in the victory condition.", required = true)
    private int duration;

    private BukkitTask durationTask;

    @Override
    public void onStart() {
        this.durationTask = Bukkit.getScheduler().runTaskLater(
                this.competition.getArena().getPlugin(),
                this::advanceToNextPhase,
                this.duration * 20L
        );
    }

    public void onVictory(Set<ArenaPlayer> victors) {
        // Call victory event
        this.competition.getArena().getEventManager().callEvent(new ArenaVictoryEvent(
                this.competition.getArena(),
                this.competition,
                victors
        ));

        // Call the lose event
        Set<ArenaPlayer> losers = new HashSet<>(this.competition.getPlayers());
        losers.removeAll(victors);

        this.competition.getArena().getEventManager().callEvent(new ArenaLoseEvent(
                this.competition.getArena(),
                this.competition,
                losers
        ));
    }

    public void onDraw() {
        // Check to see if we have any victors from other conditions
        Set<ArenaPlayer> potentialVictors = this.competition.getVictoryManager().identifyPotentialVictors();
        if (!potentialVictors.isEmpty()) {
            this.onVictory(potentialVictors);
            return;
        }

        // Call draw event
        this.competition.getArena().getEventManager().callEvent(new ArenaDrawEvent(
                this.competition.getArena(),
                this.competition
        ));
    }

    @Override
    public void onComplete() {
        this.durationTask = null;
    }
}
