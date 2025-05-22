/**
 * Created by Matthew Stewart on 10/30/2017 10:46:59 AM
 */
package com.filter.helper;


import android.annotation.SuppressLint;
import android.content.Context;

import com.filter.transition.GLFilter;
import com.filter.transition.glCanvas.GlScaleImage;
import com.filter.transition.glFilter.GPUImageVignetteFilter;
import com.filter.transition.glFilter.JSAmaroFilter;
import com.filter.transition.glFilter.JSAntiqueFilter;
import com.filter.transition.glFilter.JSBlackCatFilter;
import com.filter.transition.glFilter.JSLomoFilter;
import com.filter.transition.glFilter.JSToneCurved;
import com.filter.transition.glFilter.MagicBlackCatFilter;
import com.filter.transition.glFilter.MagicBrannanFilter;
import com.filter.transition.glFilter.MagicCalmFilter;
import com.filter.transition.glFilter.MagicCoolFilter;
import com.filter.transition.glFilter.MagicEarlyBirdFilter;
import com.filter.transition.glFilter.MagicEmeraldFilter;
import com.filter.transition.glFilter.MagicEvergreenFilter;
import com.filter.transition.glFilter.MagicInkwellFilter;
import com.filter.transition.glFilter.MagicKevinFilter;
import com.filter.transition.glFilter.MagicLatteFilter;
import com.filter.transition.glFilter.MagicN1977Filter;
import com.filter.transition.glFilter.MagicNashvilleFilter;
import com.filter.transition.glFilter.MagicNostalgiaFilter;
import com.filter.transition.glFilter.MagicPixarFilter;
import com.filter.transition.glFilter.MagicRomanceFilter;
import com.filter.transition.glFilter.MagicSakuraFilter;
import com.filter.transition.glFilter.MagicSkinWhitenFilter;
import com.filter.transition.glFilter.MagicSunriseFilter;
import com.filter.transition.glFilter.MagicSweetsFilter;
import com.filter.transition.glFilter.MagicToneCurvedFilter;
import com.filter.transition.glFilter.MagicValenciaFilter;
import com.filter.transition.glFilter.MagicWaldenFilter;
import com.filter.transition.glFilter.MagicWarmFilter;
import com.filter.transition.glFilter.MagicWhiteCatFilter;
import com.filter.transition.glFilter.MagicXproIIFilter;
import com.transition.R;


public class FilterManager {

    @SuppressLint("StaticFieldLeak")
    private static FilterManager instance;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private FilterManager() {
    }

    public static void init(Context ct) {
        context = ct;
        instance = new FilterManager();
    }

    public static FilterManager getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    public String getFilterString(MagicFilterType type) {
        return switch (type) {
            case AFTERGLOW, ALICE_IN_WONDERLAND, AMBERS, AUGUST_MARCH, AURORA, BABY_FACE,
                 BLOOD_ORANGE, BLUE_POPPIES, BLUE_YELLOW_FIELD, CAROUSEL, COLD_DESERT, COLD_HEART,
                 COUNTRY, DIGITAL_FILM, DOCUMENTARY, FOGY_BLUE, FRESH_BLUE, GHOSTS_IN_YOUR_HEADY,
                 GOLDEN_HOUR, GOOD_LUCK_CHARM, GREEN_ENVY, HUMMING_BIRDS, KISS_KISS,
                 LEFT_HAND_BLUES, LIGHT_PARADES, LULLABYE, MOTH_WINGS, MYSTERY, OLD_POSTCARDS,
                 PEACOCK_FEATHERS, PISTOL, RAGDOLL, ROSE_THORNS_TWO, SNOW_WHITE, SPARKS,
                 TOES_IN_THE_OCEAN, TONE_LEMON, WILD_AT_HEART, WINDOW_WARMTH -> type + "";
            case ACID -> "filter_shader/filter_acid.glsl";
            case ANIME -> "effect_shader/filter_anime.glsl";
            case BILATERAL -> "filter_shader/filter_bilateral.glsl";
            case BITMAP_ON_BITMAP -> "filter_shader/filter_bitmap_on_bitmap.glsl";
            case BLUR -> "filter_shader/filter_blur.glsl";
            case BLUR_COLORFUL -> "filter_shader/filter_blur_colorful.glsl";
            case CARTOON -> "filter_shader/filter_cartoon.glsl";
            case CHAPLIN -> "effect_shader/filter_chaplin1.glsl";
            case CHASE_PICTURES -> "effect_shader/filter_chase_pictures.glsl";
            case CHROMA -> "effect_shader/filter_chroma.glsl";
            case DEFORM -> "effect_shader/filter_deform.glsl";
            case DROSTE -> "effect_shader/filter_droste.glsl";
            case FILL_COLOR -> "filter_shader/filter_fill_color.glsl";
            case FISH_EYE -> "effect_shader/filter_fisheye.glsl";
            case FADE_BLACK -> "effect_shader/fade_black.glsl";
            case GAUSSIAN_BLUR -> "effect_shader/filter_gaussian_blur.glsl";
            case GLITCH_BLACK -> "effect_shader/filter_glitch_black.glsl";
            case GLITCH_WHITE -> "effect_shader/filter_glitch_white.glsl";
            case GLITCH_1 -> "effect_shader/filter_glitch1.glsl";
            case GLITCH_2 -> "effect_shader/filter_glitch2.glsl";
            case GLITCH_3 -> "effect_shader/filter_glitch3.glsl";
            case GLITCH_4 -> "effect_shader/filter_glitch4.glsl";
            case GLITCH_5 -> "effect_shader/filter_glitch5.glsl";
            case GLITCH_6 -> "effect_shader/filter_glitch6.glsl";
            case GLITCH_7 -> "effect_shader/filter_glitch7.glsl";
            case GLITCH_8 -> "effect_shader/filter_glitch8.glsl";
            case GLITCH_9 -> "effect_shader/filter_glitch9.glsl";
            case GLITCH_10 -> "effect_shader/filter_glitch10.glsl";
            case EFFECT_ZOOM_RAN -> "effect_shader/effect_simple_zoom.glsl";
            case GLITCH_GREEN -> "effect_shader/filter_glitch_green.glsl";
            case GRADIENT_MODEL -> "effect_shader/filter_gradient_model.glsl";
            case GRAIN -> "effect_shader/filter_grain.glsl";
            case GRAY -> "filter_shader/filter_gray.glsl";
            case GRAY_WAVES -> "filter_shader/filter_gray_waves.glsl";
            case HALLOWEEN -> "effect_shader/filter_halloween.glsl";
            case HORROR -> "filter_shader/filter_horror.glsl";
            case LIGHT -> "effect_shader/filter_light.glsl";
            case MIRROR -> "effect_shader/filter_mirror.glsl";
            case FLIP -> "effect_shader/filter_flip.glsl";
            case MORE_POINT -> "filter_shader/filter_more_point.glsl";
            case MOSH -> "effect_shader/filter_mosh.glsl";
            case NEGATIVE -> "filter_shader/filter_negative.glsl";
            case NOISE -> "effect_shader/filter_noise.glsl";
            case NOSTALGIC -> "filter_shader/filter_nostalgic.glsl";
            case ORANGE -> "filter_shader/filter_orange.glsl";
            case OUT_OF_DOUYIN -> "effect_shader/filter_out_of_douyin.glsl";
            case GLITCH_DOUYIN -> "effect_shader/filter_glitch_douyin.glsl";
            case OUT_OF_BODY -> "effect_shader/filter_out_of_body.glsl";
            case OVERLAY -> "filter_shader/filter_overlay.glsl";
            case POST_GLOW -> "filter_shader/filter_post_glow.glsl";
            case PREVIEW_SHADER -> "filter_shader/filter_preview_shader.glsl";
            case RAIN -> "effect_shader/filter_rain.glsl";
            case REPEAT -> "effect_shader/filter_repeat.glsl";
            case REVERSE -> "effect_shader/filter_reverse.glsl";
            case ROTATE_BLUR -> "effect_shader/filter_rotate_blur.glsl";
            case ROTATE_GRADIENT -> "effect_shader/filter_rotate_gradient.glsl";
            case ROTATE_POINT -> "filter_shader/filter_rotate_point.glsl";
            case ROTATE_ILLUSION -> "effect_shader/filter_rotation_illusion.glsl";
            case SHARPEN -> "filter_shader/filter_sharpen.glsl";
            case SKETCH_ONE -> "filter_shader/filter_sketch_one.glsl";
            case SLIM_BLUE -> "filter_shader/filter_slim_blue.glsl";
            case SLIM_SHADOW -> "filter_shader/filter_slim_shadow.glsl";
            case SPIRAL -> "effect_shader/filter_spiral.glsl";
            case SLIM_REPEAT_4 -> "effect_shader/filter_repeat_4.glsl";
            case SLIM_SPLIT_1 -> "effect_shader/filter_split1.glsl";
            case SLIM_SPLIT_2 -> "effect_shader/filter_split2.glsl";
            case SLIM_SPLIT_3 -> "effect_shader/filter_split3.glsl";
            case SLIM_SPLIT_4 -> "effect_shader/filter_split4.glsl";
            case SLIM_SPLIT_5 -> "effect_shader/filter_split5.glsl";
            case SLIM_SPLIT_6 -> "effect_shader/filter_splite_6.glsl";
            case STORY_BOARD_EFFECTS -> "filter_shader/filter_storyboard_effects.glsl";
            case STROBE -> "effect_shader/filter_strobe.glsl";
            case SWAY -> "effect_shader/filter_sway.glsl";
            case VIGNETTER -> "filter_shader/filter_vignetter.glsl";
            case VINTAGE -> "filter_shader/filter_vintage.glsl";
            case VSPISHKA -> "filter_shader/filter_vspishka.glsl";
            case WATER_DRUM -> "effect_shader/filter_water_drum.glsl";
            case WHITE_BALANCER -> "filter_shader/filter_white_balancer.glsl";
            case WRAP -> "effect_shader/filter_wrap.glsl";
            case EFFECT_ZOOM_BLUR -> "effect_shader/effect_zoom_blur.glsl";
            case EFFECT_PHERE_REFRACTION -> "effect_shader/effect_phere_refraction.glsl";
            case CANVAS_SCALE_IMAGE -> "CANVAS_SCALE_IMAGE";
            case EFFECT_ZOOM -> "effect_shader/filter_zoom.glsl";
            case EFFECT_TRANSLATE -> "effect_shader/filter_translate.glsl";
            case EFFECT_ROTATE -> "effect_shader/filter_rotate.glsl";
            case EFFECT_TEST -> "effect_shader/filter_test.glsl";
            case EFFECT_ROLL -> "effect_shader/effect_roll.glsl";
            case FILM_BORDER -> "effect_shader/filter_film_border.glsl";
            case EFFECT_DOUBLE_ZOOM -> "effect_shader/effect_double_zoom.glsl";
            case EFFECT_SHAKE -> "effect_shader/effect_shake.glsl";
            case FILTER_BUTTERFLY -> "effect_shader/filter_butterfly.glsl";
            case EFFECT_BULGE_DISTORTION -> "effect_shader/effect_bulge_distortion.glsl";
            default -> "filter_shader/filter_default.glsl";
        };
    }

    public String getTransitionString(MagicFilterType type) {
        return switch (type) {
            case TRANSITION_ANGULAR -> "transition_shader/transition_angular.glsl";
            case TRANSITION_BLINDS -> "transition_shader/transition_blinds.glsl";
            case TRANSITION_BOW_TIE_HORIZONTAL ->
                    "transition_shader/transition_bow_tie_horizontal.glsl";
            case TRANSITION_BOW_TIE_VERTICAL ->
                    "transition_shader/transition_bow_tie_vertical.glsl";
            case TRANSITION_BURN -> "transition_shader/transition_burn.glsl";
            case TRANSITION_BUTTERFLY_WAVE_SCRAWLER ->
                    "transition_shader/transition_butterfly_wave_scrawler.glsl";
            case TRANSITION_CANNABISLEAF -> "transition_shader/transition_cannabisleaf.glsl";
            case TRANSITION_CIRCLE_CROP -> "transition_shader/transition_circle_crop.glsl";
            case TRANSITION_CIRCLE_OPEN -> "transition_shader/transition_circle_open.glsl";
            case TRANSITION_COLOR_PHASE -> "transition_shader/transition_color_phase.glsl";
            case TRANSITION_COLOUR_DISTANCE -> "transition_shader/transition_colour_distance.glsl";
            case TRANSITION_CRAZY_PARAMETRIX ->
                    "transition_shader/transition_crazy_parametrix_fun.glsl";
            case TRANSITION_CROSS_ZOOM -> "transition_shader/transition_cross_zoom.glsl";
            case TRANSITION_CROSS_HATCH -> "transition_shader/transition_crosshatch.glsl";
            case TRANSITION_CROSS_WARP -> "transition_shader/transition_crosswarp.glsl";
            case TRANSITION_CUBE -> "transition_shader/transition_cube.glsl";
            case TRANSITION_DIRECTIONAL -> "transition_shader/transition_directional.glsl";
            case TRANSITION_DIRECTIONAL_HORIZONTAL ->
                    "transition_shader/transition_directional_horizontal.glsl";
            case TRANSITION_DIRECTIONAL_WIPE ->
                    "transition_shader/transition_directional_wipe.glsl";
            case TRANSITION_DIRECTIONAL_WARP -> "transition_shader/transition_directionalwarp.glsl";
            case TRANSITION_DOOM_SCREEN -> "transition_shader/transition_doom_screen.glsl";
            case TRANSITION_DOOR_WAY -> "transition_shader/transition_doorway.glsl";
            case TRANSITION_DREAMY -> "transition_shader/transition_dreamy.glsl";
            case TRANSITION_DREAMY_ZOOM -> "transition_shader/transition_dreamy_zoom.glsl";
            case TRANSITION_FADE -> "transition_shader/transition_fade.glsl";
            case TRANSITION_FADE_BLACK -> "transition_shader/transition_fade_back.glsl";
            case TRANSITION_FADE_WHITE -> "transition_shader/transition_fade_white.glsl";
            case TRANSITION_FADE_GRAY_SCALE -> "transition_shader/transition_fade_gray_scale.glsl";
            case TRANSITION_FADE_HORIZONTAL -> "transition_shader/transition_fade_horizontal.glsl";
            case TRANSITION_FLY_EYE -> "transition_shader/transition_flyeye.glsl";
            case TRANSITION_GLITCH_DISPLACE -> "transition_shader/transition_glitch_displace.glsl";
            case TRANSITION_GLITCH_MOMORIES -> "transition_shader/transition_glitch_memories.glsl";
            case TRANSITION_GRID_FLIP -> "transition_shader/transition_grid_flip.glsl";
            case TRANSITION_HEXAGONALIZE -> "transition_shader/transition_hexagonalize.glsl";
            case TRANSITION_INVERTED_PAGE_CURL ->
                    "transition_shader/transition_inverted_page_curl.glsl";
            case TRANSITION_KALEIDOSCOPE -> "transition_shader/transition_kaleidoscope.glsl";
            case TRANSITION_LINEAR_BLUR -> "transition_shader/transition_linear_blur.glsl";
            case TRANSITION_LUMA -> "transition_shader/transition_luma.glsl";
            case TRANSITION_LUMINANCE_MELT -> "transition_shader/transition_luminance_melt.glsl";
            case TRANSITION_MASK -> "transition_shader/transition_mask.glsl";
            case TRANSITION_MORPH -> "transition_shader/transition_morph.glsl";
            case TRANSITION_MOSAIC -> "transition_shader/transition_mosaic.glsl";
            case TRANSITION_MULTIPLY_BLEND -> "transition_shader/transition_multiply_blend.glsl";
            case TRANSITION_OFFSET -> "transition_shader/transition_offset.glsl";
            case TRANSITION_OUT_OF_BODY -> "transition_shader/transition_out_of_body.glsl";
            case TRANSITION_PERLIN -> "transition_shader/transition_perlin.glsl";
            case TRANSITION_PIN_WHEEL -> "transition_shader/transition_pinwheel.glsl";
            case TRANSITION_PIXELIZE -> "transition_shader/transition_pixelize.glsl";
            case TRANSITION_POLAR_FUNCTION -> "transition_shader/transition_polar_function.glsl";
            case TRANSITION_POLAR_DOTS_CURTAIN ->
                    "transition_shader/transition_polka_dots_curtain.glsl";
            case TRANSITION_RADIAL -> "transition_shader/transition_radial.glsl";
            case TRANSITION_RANDOM_SQUARES -> "transition_shader/transition_random_squares.glsl";
            case TRANSITION_RIPPLE -> "transition_shader/transition_ripple.glsl";
            case TRANSITION_ROTATE_SCALE_FADE ->
                    "transition_shader/transition_rotate_scale_fade.glsl";
            case TRANSITION_SIMPLE_ZOOM -> "transition_shader/transition_simple_zoom.glsl";
            case TRANSITION_SIMPLE_ZOOM_IN -> "transition_shader/transition_simple_zoom_in.glsl";
            case TRANSITION_SQUARES_WIRES -> "transition_shader/transition_squares_wire.glsl";
            case TRANSITION_SQUEEZE -> "transition_shader/transition_squeeze.glsl";
            case TRANSITION_STEREO_VIEWER -> "transition_shader/transition_stereo_viewer.glsl";
            case TRANSITION_SWIRL -> "transition_shader/transition_swirl.glsl";
            case TRANSITION_UNDULATING_BURN_OUT ->
                    "transition_shader/transition_undulating_burn_out.glsl";
            case TRANSITION_WAP -> "transition_shader/transition_wap.glsl";
            case TRANSITION_WATER_DROP -> "transition_shader/transition_water_drop.glsl";
            case TRANSITION_WATER_RIPPLE -> "transition_shader/transition_water_ripple.glsl";
            case TRANSITION_WIND -> "transition_shader/transition_wind.glsl";
            case TRANSITION_WINDOW_BLINDS -> "transition_shader/transition_window_blinds.glsl";
            case TRANSITION_WINDOW_SPLICE -> "transition_shader/transition_window_slice.glsl";
            case TRANSITION_WIPE_LEFT -> "transition_shader/transition_wipe_left.glsl";
            case TRANSITION_WIPE_RIGHT -> "transition_shader/transition_wipe_right.glsl";
            case TRANSITION_WIPE_UP -> "transition_shader/transition_wipe_up.glsl";
            case TRANSITION_CUBE_VERTICAL -> "transition_shader/transition_cube_vertical.glsl";
            case TRANSITION_DIP_TO_COLOR -> "transition_shader/transition_dip_to_color.glsl";
            case TRANSITION_FLIP -> "transition_shader/transition_flip.glsl";
            case TRANSITION_SLIDE -> "transition_shader/transition_slide.glsl";
            case EFFECT_TEST -> "effect_shader/filter_test.glsl";
            default -> "transition_shader/transition_default.glsl";
        };
    }

    public GLFilter getFilter(MagicFilterType type, Context context) {
        GLFilter glFilter = new GLFilter();
        JSToneCurved jsToneCurved = new JSToneCurved(context);
        String filter = "filter_shader/filter_default.glsl";
        switch (type) {
            case AFTERGLOW:
                jsToneCurved.setCurveFile(R.raw.afterglow);
                return jsToneCurved;
            case ALICE_IN_WONDERLAND:
                jsToneCurved.setCurveFile(R.raw.alice_in_wonderland);
                return jsToneCurved;
            case AMBERS:
                jsToneCurved.setCurveFile(R.raw.ambers);
                return jsToneCurved;
            case AUGUST_MARCH:
                jsToneCurved.setCurveFile(R.raw.august_march);
                return jsToneCurved;
            case AURORA:
                jsToneCurved.setCurveFile(R.raw.aurora);
                return jsToneCurved;
            case BABY_FACE:
                jsToneCurved.setCurveFile(R.raw.baby_face);
                return jsToneCurved;
            case BLOOD_ORANGE:
                jsToneCurved.setCurveFile(R.raw.blood_orange);
                return jsToneCurved;
            case BLUE_POPPIES:
                jsToneCurved.setCurveFile(R.raw.blue_poppies);
                return jsToneCurved;
            case BLUE_YELLOW_FIELD:
                jsToneCurved.setCurveFile(R.raw.blue_yellow_field);
                return jsToneCurved;
            case CAROUSEL:
                jsToneCurved.setCurveFile(R.raw.carousel);
                return jsToneCurved;
            case COLD_DESERT:
                jsToneCurved.setCurveFile(R.raw.cold_desert);
                return jsToneCurved;
            case COLD_HEART:
                jsToneCurved.setCurveFile(R.raw.cold_heart);
                return jsToneCurved;
            case COUNTRY:
                jsToneCurved.setCurveFile(R.raw.country);
                return jsToneCurved;
            case DIGITAL_FILM:
                jsToneCurved.setCurveFile(R.raw.digital_film);
                return jsToneCurved;
            case DOCUMENTARY:
                jsToneCurved.setCurveFile(R.raw.documentary);
                return jsToneCurved;
            case FOGY_BLUE:
                jsToneCurved.setCurveFile(R.raw.fogy_blue);
                return jsToneCurved;
            case FRESH_BLUE:
                jsToneCurved.setCurveFile(R.raw.fresh_blue);
                return jsToneCurved;
            case GHOSTS_IN_YOUR_HEADY:
                jsToneCurved.setCurveFile(R.raw.ghosts_in_your_head);
                return jsToneCurved;
            case GOLDEN_HOUR:
                jsToneCurved.setCurveFile(R.raw.golden_hour);
                return jsToneCurved;
            case GOOD_LUCK_CHARM:
                jsToneCurved.setCurveFile(R.raw.good_luck_charm);
                return jsToneCurved;
            case GREEN_ENVY:
                jsToneCurved.setCurveFile(R.raw.green_envy);
                return jsToneCurved;
            case HUMMING_BIRDS:
                jsToneCurved.setCurveFile((R.raw.humming_birds));
                return jsToneCurved;
            case KISS_KISS:
                jsToneCurved.setCurveFile((R.raw.kiss_kiss));
                return jsToneCurved;
            case LEFT_HAND_BLUES:
                jsToneCurved.setCurveFile((R.raw.left_hand_blues));
                return jsToneCurved;
            case LIGHT_PARADES:
                jsToneCurved.setCurveFile((R.raw.light_parades));
                return jsToneCurved;
            case LULLABYE:
                jsToneCurved.setCurveFile((R.raw.lullabye));
                return jsToneCurved;
            case MOTH_WINGS:
                jsToneCurved.setCurveFile((R.raw.moth_wings));
                return jsToneCurved;
            case MYSTERY:
                jsToneCurved.setCurveFile((R.raw.mystery));
                return jsToneCurved;
            case OLD_POSTCARDS:
                jsToneCurved.setCurveFile((R.raw.old_postcards));
                return jsToneCurved;
            case PEACOCK_FEATHERS:
                jsToneCurved.setCurveFile((R.raw.peacock_feathers));
                return jsToneCurved;
            case PISTOL:
                jsToneCurved.setCurveFile((R.raw.pistol));
                return jsToneCurved;
            case RAGDOLL:
                jsToneCurved.setCurveFile((R.raw.ragdoll));
                return jsToneCurved;
            case ROSE_THORNS_TWO:
                jsToneCurved.setCurveFile((R.raw.rose_thorns_two));
                return jsToneCurved;
            case SNOW_WHITE:
                jsToneCurved.setCurveFile((R.raw.snow_white));
                return jsToneCurved;
            case SPARKS:
                jsToneCurved.setCurveFile((R.raw.sparks));
                return jsToneCurved;
            case TOES_IN_THE_OCEAN:
                jsToneCurved.setCurveFile((R.raw.toes_in_the_ocean));
                return jsToneCurved;
            case TONE_LEMON:
                jsToneCurved.setCurveFile((R.raw.tone_lemon));
                return jsToneCurved;
            case WILD_AT_HEART:
                jsToneCurved.setCurveFile((R.raw.wild_at_heart));
                return jsToneCurved;
            case WINDOW_WARMTH:
                jsToneCurved.setCurveFile((R.raw.window_warmth));
                return jsToneCurved;
            case ACID:
                filter = "filter_shader/filter_acid.glsl";
                break;
            case ANIME:
                filter = "effect_shader/filter_anime.glsl";
                break;
            case BILATERAL:
                filter = "filter_shader/filter_bilateral.glsl";
                break;
            case BITMAP_ON_BITMAP:
                filter = "filter_shader/filter_bitmap_on_bitmap.glsl";
                break;
            case BLUR:
                filter = "filter_shader/filter_blur.glsl";
                break;
            case GLITCH_WHITE:
                filter = "effect_shader/filter_glitch_white.glsl";
                break;
            case BLUR_COLORFUL:
                filter = "filter_shader/filter_blur_colorful.glsl";
                break;
            case CARTOON:
                filter = "filter_shader/filter_cartoon.glsl";
                break;
            case CHAPLIN:
                filter = "effect_shader/filter_chaplin1.glsl";
                break;
            case CHASE_PICTURES:
                filter = "effect_shader/filter_chase_pictures.glsl";
                break;
            case CHROMA:
                filter = "effect_shader/filter_chroma.glsl";
                break;
            case DEFORM:
                filter = "effect_shader/filter_deform.glsl";
                break;
            case DROSTE:
                filter = "effect_shader/filter_droste.glsl";
                break;
            case FILL_COLOR:
                filter = "filter_shader/filter_fill_color.glsl";
                break;
            case FISH_EYE:
                filter = "effect_shader/filter_fisheye.glsl";
                break;
            case FADE_BLACK:
                filter = "effect_shader/fade_black.glsl";
                break;
            case GAUSSIAN_BLUR:
                filter = "effect_shader/filter_gaussian_blur.glsl";
                break;
            case MOSH:
                filter = "effect_shader/filter_mosh.glsl";
                break;
            case NOISE:
                filter = "effect_shader/filter_noise.glsl";
                break;
            case GLITCH_1:
                filter = "effect_shader/filter_glitch1.glsl";
                break;
            case GLITCH_2:
                filter = "effect_shader/filter_glitch2.glsl";
                break;
            case GLITCH_3:
                filter = "effect_shader/filter_glitch3.glsl";
                break;
            case GLITCH_4:
                filter = "effect_shader/filter_glitch4.glsl";
                break;
            case GLITCH_5:
                filter = "effect_shader/filter_glitch5.glsl";
                break;
            case GLITCH_6:
                filter = "effect_shader/filter_glitch6.glsl";
                break;
            case GLITCH_7:
                filter = "effect_shader/filter_glitch7.glsl";
                break;
            case GLITCH_8:
                filter = "effect_shader/filter_glitch8.glsl";
                break;
            case GLITCH_9:
                filter = "effect_shader/filter_glitch9.glsl";
                break;
            case GLITCH_10:
                filter = "effect_shader/filter_glitch10.glsl";
                break;
            case GLITCH_GREEN:
                filter = "effect_shader/filter_glitch_green.glsl";
                break;
            case GLITCH_BLACK:
                filter = "effect_shader/filter_glitch_black.glsl";
                break;
            case GRADIENT_MODEL:
                filter = "effect_shader/filter_gradient_model.glsl";
                break;
            case GRAIN:
                filter = "effect_shader/filter_grain.glsl";
                break;
            case GRAY:
                filter = "filter_shader/filter_gray.glsl";
                break;
            case GRAY_WAVES:
                filter = "filter_shader/filter_gray_waves.glsl";
                break;
            case HALLOWEEN:
                filter = "effect_shader/filter_halloween.glsl";
                break;
            case HORROR:
                filter = "filter_shader/filter_horror.glsl";
                break;
            case LIGHT:
                filter = "effect_shader/filter_light.glsl";
                break;
            case MIRROR:
                filter = "effect_shader/filter_mirror.glsl";
                break;
            case FLIP:
                filter = "effect_shader/filter_flip.glsl";
                break;
            case MORE_POINT:
                filter = "filter_shader/filter_more_point.glsl";
                break;
            case NEGATIVE:
                filter = "filter_shader/filter_negative.glsl";
                break;
            case NOSTALGIC:
                filter = "filter_shader/filter_nostalgic.glsl";
                break;
            case ORANGE:
                filter = "filter_shader/filter_orange.glsl";
                break;
            case OUT_OF_DOUYIN:
                filter = "effect_shader/filter_out_of_douyin.glsl";
                break;
            case GLITCH_DOUYIN:
                filter = "effect_shader/filter_glitch_douyin.glsl";
                break;
            case OUT_OF_BODY:
                filter = "effect_shader/filter_out_of_body.glsl";
                break;
            case OVERLAY:
                filter = "filter_shader/filter_overlay.glsl";
                break;
            case POST_GLOW:
                filter = "filter_shader/filter_post_glow.glsl";
                break;
            case PREVIEW_SHADER:
                filter = "filter_shader/filter_preview_shader.glsl";
                break;
            case RAIN:
                filter = "effect_shader/filter_rain.glsl";
                break;
            case REPEAT:
                filter = "effect_shader/filter_repeat.glsl";
                break;
            case REVERSE:
                filter = "effect_shader/filter_reverse.glsl";
                break;
            case ROTATE_BLUR:
                filter = "effect_shader/filter_rotate_blur.glsl";
                break;
            case ROTATE_GRADIENT:
                filter = "effect_shader/filter_rotate_gradient.glsl";
                break;
            case ROTATE_POINT:
                filter = "filter_shader/filter_rotate_point.glsl";
                break;
            case ROTATE_ILLUSION:
                filter = "effect_shader/filter_rotation_illusion.glsl";
                break;
            case SHARPEN:
                filter = "filter_shader/filter_sharpen.glsl";
                break;
            case SKETCH_ONE:
                filter = "filter_shader/filter_sketch_one.glsl";
                break;
            case SLIM_BLUE:
                filter = "filter_shader/filter_slim_blue.glsl";
                break;
            case SLIM_SHADOW:
                filter = "filter_shader/filter_slim_shadow.glsl";
                break;
            case SPIRAL:
                filter = "effect_shader/filter_spiral.glsl";
                break;
            case SLIM_SPLIT_1:
                filter = "effect_shader/filter_split1.glsl";
                break;
            case SLIM_SPLIT_2:
                filter = "effect_shader/filter_split2.glsl";
                break;
            case SLIM_SPLIT_3:
                filter = "effect_shader/filter_split3.glsl";
                break;
            case SLIM_SPLIT_4:
                filter = "effect_shader/filter_split4.glsl";
                break;
            case SLIM_SPLIT_5:
                filter = "effect_shader/filter_split5.glsl";
                break;
            case SLIM_SPLIT_6:
                filter = "effect_shader/filter_splite_6.glsl";
                break;
            case SLIM_REPEAT_4:
                filter = "effect_shader/filter_repeat_4.glsl";
                break;
            case STORY_BOARD_EFFECTS:
                filter = "filter_shader/filter_storyboard_effects.glsl";
                break;
            case STROBE:
                filter = "effect_shader/filter_strobe.glsl";
                break;
            case SWAY:
                filter = "effect_shader/filter_sway.glsl";
                break;
            case VIGNETTER:
                filter = "filter_shader/filter_vignetter.glsl";
                break;
            case VINTAGE:
                filter = "filter_shader/filter_vintage.glsl";
                break;
            case VSPISHKA:
                filter = "filter_shader/filter_vspishka.glsl";
                break;
            case WATER_DRUM:
                filter = "effect_shader/filter_water_drum.glsl";
                break;
            case WHITE_BALANCER:
                filter = "filter_shader/filter_white_balancer.glsl";
                break;
            case WRAP:
                filter = "effect_shader/filter_wrap.glsl";
                break;
            case EFFECT_ZOOM_BLUR:
                filter = "effect_shader/effect_zoom_blur.glsl";
                break;
            case EFFECT_PHERE_REFRACTION:
                filter = "effect_shader/effect_phere_refraction.glsl";
                break;
            case EFFECT_BULGE_DISTORTION:
                filter = "effect_shader/effect_bulge_distortion.glsl";
                break;
            case EFFECT_ZOOM:
                filter = "effect_shader/filter_zoom.glsl";
                break;
            case EFFECT_TRANSLATE:
                filter = "effect_shader/filter_translate.glsl";
                break;
            case EFFECT_ROTATE:
                filter = "effect_shader/filter_rotate.glsl";
                break;
            case EFFECT_TEST:
                filter = "effect_shader/filter_test.glsl";
                break;
            case EFFECT_ROLL:
                filter = "effect_shader/effect_roll.glsl";
                break;
            case FILM_BORDER:
                filter = "effect_shader/filter_film_border.glsl";
                break;
            case EFFECT_DOUBLE_ZOOM:
                filter = "effect_shader/effect_double_zoom.glsl";
                break;
            case EFFECT_SHAKE:
                filter = "effect_shader/effect_shake.glsl";
                break;
            case FILTER_BUTTERFLY:
                filter = "effect_shader/filter_butterfly.glsl";
                break;
            case CANVAS_SCALE_IMAGE:
                return new GlScaleImage(context);
            case MagicXproIIFilter:
                return new MagicXproIIFilter();
            case MagicWhiteCatFilter:
                return new MagicWhiteCatFilter();
            case MagicWarmFilter:
                return new MagicWarmFilter();
            case MagicWaldenFilter:
                return new MagicWaldenFilter();
            case MagicValenciaFilter:
                return new MagicValenciaFilter();
            case MagicToneCurvedFilter:
                return new MagicToneCurvedFilter();
            case MagicSweetsFilter:
                return new MagicSweetsFilter();
            case MagicSunriseFilter:
                return new MagicSunriseFilter();
            case MagicSkinWhitenFilter:
                return new MagicSkinWhitenFilter();
            case MagicSakuraFilter:
                return new MagicSakuraFilter();
            case MagicRomanceFilter:
                return new MagicRomanceFilter();
            case MagicPixarFilter:
                return new MagicPixarFilter();
            case MagicNostalgiaFilter:
                return new MagicNostalgiaFilter();
            case MagicNashvilleFilter:
                return new MagicNashvilleFilter();
            case MagicN1977Filter:
                return new MagicN1977Filter();
            case MagicLatteFilter:
                return new MagicLatteFilter();
            case MagicKevinFilter:
                return new MagicKevinFilter();
            case MagicInkwellFilter:
                return new MagicInkwellFilter();
            case MagicEvergreenFilter:
                return new MagicEvergreenFilter();
            case MagicEmeraldFilter:
                return new MagicEmeraldFilter();
            case MagicEarlyBirdFilter:
                return new MagicEarlyBirdFilter();
            case MagicCoolFilter:
                return new MagicCoolFilter();
            case MagicCalmFilter:
                return new MagicCalmFilter();
            case MagicBrannanFilter:
                return new MagicBrannanFilter();
            case MagicBlackCatFilter:
                return new MagicBlackCatFilter();
            case JSLomoFilter:
                return new JSLomoFilter();
            case JSBlackCatFilter:
                return new JSBlackCatFilter();
            case JSAntiqueFilter:
                return new JSAntiqueFilter();
            case JSAmaroFilter:
                return new JSAmaroFilter();
            case GPUImageVignetteFilter:
                return new GPUImageVignetteFilter();
        }
        glFilter.setFragmentShader(context, filter);
        return glFilter;
    }

}
