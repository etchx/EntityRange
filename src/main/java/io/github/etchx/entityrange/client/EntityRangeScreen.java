package io.github.etchx.entityrange.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;

import java.io.IOException;

import static io.github.etchx.entityrange.client.EntityRangeClient.CONFIG;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitDistance;
import static io.github.etchx.entityrange.client.EntityRangeConfig.distanceX;
import static io.github.etchx.entityrange.client.EntityRangeConfig.distanceY;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hitX;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hitY;

@Environment(EnvType.CLIENT)
public class EntityRangeScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable("EntityRange Layout");
    private final Screen parent;
    private DraggableTextWidget distanceWidget;
    private DraggableTextWidget hitWidget;

    public EntityRangeScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    protected void init() {
        distanceWidget = new DraggableTextWidget(this.width, this.height, distanceX, distanceY,
                Text.translatable(String.format("%.3f", raycastHitDistance)), this.client.textRenderer);
        hitWidget = new DraggableTextWidget(this.width, this.height, hitX, hitY,
                Text.translatable(String.format("Last hit: %.3f", lastHit)), this.client.textRenderer);
        this.addDrawableChild(distanceWidget);
        this.addDrawableChild(hitWidget);
    }

    @Override
    public void close() {
        distanceX = distanceWidget.percentX;
        distanceY = distanceWidget.percentY;
        hitX = hitWidget.percentX;
        hitY = hitWidget.percentY;
        try {
            EntityRangeConfig.writeChanges(CONFIG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.client.setScreen(this.parent);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
    }

    public static class DraggableTextWidget extends TextWidget {
        protected float percentX;
        protected float percentY;
        protected int screenWidth;
        protected int screenHeight;

        public DraggableTextWidget(int screenWidth, int screenHeight, float x, float y, Text message, TextRenderer textRenderer) {
            super(EntityRangeUtil.getX(x, screenWidth, message, textRenderer), EntityRangeUtil.getY(y, screenHeight), textRenderer.getWidth(message), 8, message, textRenderer);
            this.active = true;
            this.percentX = x;
            this.percentY = y;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.setTooltip(Tooltip.of(Text.of(String.format("X:%.2f%% Y:%.2f%%", this.percentX, this.percentY))));
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            this.setPosition((int) mouseX - this.width / 2, (int) mouseY - this.height / 2);
            this.percentX = EntityRangeUtil.getPercentX(this.getX(), this.screenWidth, this.getMessage(), this.getTextRenderer());
            this.percentY = EntityRangeUtil.getPercentY(this.getY(), this.screenHeight);
            this.setTooltip(Tooltip.of(Text.of(String.format("X:%.2f%% Y:%.2f%%", this.percentX, this.percentY))));
            super.onDrag(mouseX, mouseY, deltaX, deltaY);
        }
    }
}
