package dev.esophose.playerparticles.manager;

import dev.esophose.playerparticles.PlayerParticles;
import dev.esophose.playerparticles.event.ParticleStyleRegistrationEvent;
import dev.esophose.playerparticles.styles.DefaultStyles;
import dev.esophose.playerparticles.styles.ParticleStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;

public class ParticleStyleManager extends Manager {

    /**
     * Arrays that contain all registered styles
     */
    private List<ParticleStyle> styles;
    private List<ParticleStyle> eventStyles;

    public ParticleStyleManager(PlayerParticles playerParticles) {
        super(playerParticles);

        this.styles = new ArrayList<>();
        this.eventStyles = new ArrayList<>();

        DefaultStyles.initStyles();
    }

    @Override
    public void reload() {
        this.styles.clear();
        this.eventStyles.clear();

        // Call registration event
        // We use this event internally, so no other action needs to be done for us to register the default styles
        ParticleStyleRegistrationEvent event = new ParticleStyleRegistrationEvent();
        Bukkit.getPluginManager().callEvent(event);

        Collection<ParticleStyle> eventStyles = event.getRegisteredEventStyles().values();
        for (ParticleStyle style : event.getRegisteredStyles().values()) {
            try {
                if (style == null) {
                    throw new IllegalArgumentException("Tried to register a null style");
                }

                if (style.getInternalName() == null || style.getInternalName().trim().equals("")) {
                    throw new IllegalArgumentException("Tried to register a style with a null or empty name: '" + style.getInternalName() + "'");
                }

                for (ParticleStyle testAgainst : this.styles) {
                    if (testAgainst.equals(style)) {
                        throw new IllegalArgumentException("Tried to register the same style twice: '" + style.getInternalName() + "'");
                    } else if (testAgainst.getInternalName().equalsIgnoreCase(style.getInternalName())) {
                        throw new IllegalArgumentException("Tried to register two styles with the same internal name spelling: '" + style.getInternalName() + "'");
                    }
                }

                this.styles.add(style);

                if (eventStyles.contains(style))
                    this.eventStyles.add(style);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void disable() {

    }

    /**
     * Registers a style that is put into the plugin's update loop
     * 
     * @param style The style to add
     */
    @Deprecated
    public void registerStyle(ParticleStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("Tried to register a null style");
        }
        
        if (style.getInternalName() == null || style.getInternalName().trim().equals("")) {
            throw new IllegalArgumentException("Tried to register a style with a null or empty name: '" + style.getInternalName() + "'");
        }
        
        for (ParticleStyle testAgainst : this.styles) {
            if (testAgainst.equals(style)) {
                throw new IllegalArgumentException("Tried to register the same style twice: '" + style.getInternalName() + "'");
            } else if (testAgainst.getInternalName().equalsIgnoreCase(style.getInternalName())) {
                throw new IllegalArgumentException("Tried to register two styles with the same internal name spelling: '" + style.getInternalName() + "'");
            }
        }
        
        this.styles.add(style);
    }

    /**
     * Registers a style that isn't updated on the normal update loop
     * 
     * @param style The style to register
     */
    @Deprecated
    public void registerEventStyle(ParticleStyle style) {
        this.registerStyle(style);
        this.eventStyles.add(style);
    }

    /**
     * Returns if a given style is customly handled
     * 
     * @param style The style to check
     * @return If the style is handled in a custom manner
     */
    public boolean isEventHandled(ParticleStyle style) {
        return this.eventStyles.contains(style);
    }

    /**
     * @return A List of styles that are registered and enabled
     */
    public List<ParticleStyle> getStyles() {
        return this.styles.stream().filter(ParticleStyle::isEnabled).collect(Collectors.toList());
    }

    /**
     * @return all registered styles, regardless if they are enabled or not
     */
    public List<ParticleStyle> getStylesWithDisabled() {
        return this.styles;
    }

    /**
     * Updates all the timers for the particle styles to make the animations
     * 
     * Do not call this in your plugin, it will mess with other styles
     */
    public void updateTimers() {
        for (ParticleStyle style : this.styles)
            style.updateTimers();
    }

}
