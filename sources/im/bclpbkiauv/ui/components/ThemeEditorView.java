package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarLayout;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.TextColorThemeCell;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.ThemeEditorView;
import im.bclpbkiauv.ui.components.WallpaperUpdater;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ThemeEditorView {
    private static volatile ThemeEditorView Instance = null;
    /* access modifiers changed from: private */
    public ArrayList<ThemeDescription> currentThemeDesription;
    /* access modifiers changed from: private */
    public int currentThemeDesriptionPosition;
    /* access modifiers changed from: private */
    public DecelerateInterpolator decelerateInterpolator;
    /* access modifiers changed from: private */
    public EditorAlert editorAlert;
    private final int editorHeight = AndroidUtilities.dp(54.0f);
    /* access modifiers changed from: private */
    public final int editorWidth = AndroidUtilities.dp(54.0f);
    private boolean hidden;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    private SharedPreferences preferences;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo themeInfo;
    /* access modifiers changed from: private */
    public WallpaperUpdater wallpaperUpdater;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    /* access modifiers changed from: private */
    public FrameLayout windowView;

    public static ThemeEditorView getInstance() {
        return Instance;
    }

    public void destroy() {
        FrameLayout frameLayout;
        this.wallpaperUpdater.cleanup();
        if (this.parentActivity != null && (frameLayout = this.windowView) != null) {
            try {
                this.windowManager.removeViewImmediate(frameLayout);
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                if (this.editorAlert != null) {
                    this.editorAlert.dismiss();
                    this.editorAlert = null;
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            this.parentActivity = null;
            Instance = null;
        }
    }

    public class EditorAlert extends BottomSheet {
        /* access modifiers changed from: private */
        public boolean animationInProgress;
        /* access modifiers changed from: private */
        public FrameLayout bottomLayout;
        /* access modifiers changed from: private */
        public FrameLayout bottomSaveLayout;
        /* access modifiers changed from: private */
        public AnimatorSet colorChangeAnimation;
        /* access modifiers changed from: private */
        public ColorPicker colorPicker;
        private FrameLayout frameLayout;
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public ListAdapter listAdapter;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public int previousScrollPosition;
        private TextView saveButton;
        /* access modifiers changed from: private */
        public int scrollOffsetY;
        /* access modifiers changed from: private */
        public SearchAdapter searchAdapter;
        /* access modifiers changed from: private */
        public EmptyTextProgressView searchEmptyView;
        /* access modifiers changed from: private */
        public SearchField searchField;
        /* access modifiers changed from: private */
        public View[] shadow = new View[2];
        /* access modifiers changed from: private */
        public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
        /* access modifiers changed from: private */
        public Drawable shadowDrawable;
        /* access modifiers changed from: private */
        public boolean startedColorChange;
        final /* synthetic */ ThemeEditorView this$0;
        /* access modifiers changed from: private */
        public int topBeforeSwitch;

        private class SearchField extends FrameLayout {
            private View backgroundView;
            /* access modifiers changed from: private */
            public ImageView clearSearchImageView;
            /* access modifiers changed from: private */
            public EditTextBoldCursor searchEditText;

            public SearchField(Context context) {
                super(context);
                View searchBackground = new View(context);
                searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(18.0f), -854795));
                addView(searchBackground, LayoutHelper.createFrame(-1.0f, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
                ImageView searchIconImageView = new ImageView(context);
                searchIconImageView.setScaleType(ImageView.ScaleType.CENTER);
                searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
                searchIconImageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(searchIconImageView, LayoutHelper.createFrame(36.0f, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(context);
                this.clearSearchImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                ImageView imageView2 = this.clearSearchImageView;
                CloseProgressDrawable2 progressDrawable = new CloseProgressDrawable2();
                imageView2.setImageDrawable(progressDrawable);
                progressDrawable.setSide(AndroidUtilities.dp(7.0f));
                this.clearSearchImageView.setScaleX(0.1f);
                this.clearSearchImageView.setScaleY(0.1f);
                this.clearSearchImageView.setAlpha(0.0f);
                this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(-6182737, PorterDuff.Mode.MULTIPLY));
                addView(this.clearSearchImageView, LayoutHelper.createFrame(36.0f, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
                this.clearSearchImageView.setOnClickListener(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00bb: INVOKE  
                      (wrap: android.widget.ImageView : 0x00b4: IGET  (r2v15 android.widget.ImageView) = 
                      (r13v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField A[THIS])
                     im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchField.clearSearchImageView android.widget.ImageView)
                      (wrap: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM : 0x00b8: CONSTRUCTOR  (r3v8 im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM) = 
                      (r13v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField A[THIS])
                     call: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField):void type: CONSTRUCTOR)
                     android.widget.ImageView.setOnClickListener(android.view.View$OnClickListener):void type: VIRTUAL in method: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchField.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert, android.content.Context):void, dex: classes6.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00b8: CONSTRUCTOR  (r3v8 im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM) = 
                      (r13v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField A[THIS])
                     call: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchField.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert, android.content.Context):void, dex: classes6.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.this = r14
                    r13.<init>(r15)
                    android.view.View r0 = new android.view.View
                    r0.<init>(r15)
                    r1 = 1099956224(0x41900000, float:18.0)
                    int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
                    float r1 = (float) r1
                    r2 = -854795(0xfffffffffff2f4f5, float:NaN)
                    android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r1, r2)
                    r0.setBackgroundDrawable(r1)
                    r2 = -1082130432(0xffffffffbf800000, float:-1.0)
                    r3 = 1108344832(0x42100000, float:36.0)
                    r4 = 51
                    r5 = 1096810496(0x41600000, float:14.0)
                    r6 = 1093664768(0x41300000, float:11.0)
                    r7 = 1096810496(0x41600000, float:14.0)
                    r8 = 0
                    android.widget.FrameLayout$LayoutParams r1 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                    r13.addView(r0, r1)
                    android.widget.ImageView r1 = new android.widget.ImageView
                    r1.<init>(r15)
                    android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                    r1.setScaleType(r2)
                    r2 = 2131231585(0x7f080361, float:1.8079255E38)
                    r1.setImageResource(r2)
                    android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                    android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
                    r4 = -6182737(0xffffffffffa1a8af, float:NaN)
                    r2.<init>(r4, r3)
                    r1.setColorFilter(r2)
                    r5 = 1108344832(0x42100000, float:36.0)
                    r6 = 1108344832(0x42100000, float:36.0)
                    r7 = 51
                    r8 = 1098907648(0x41800000, float:16.0)
                    r9 = 1093664768(0x41300000, float:11.0)
                    r10 = 0
                    r11 = 0
                    android.widget.FrameLayout$LayoutParams r2 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
                    r13.addView(r1, r2)
                    android.widget.ImageView r2 = new android.widget.ImageView
                    r2.<init>(r15)
                    r13.clearSearchImageView = r2
                    android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
                    r2.setScaleType(r3)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    im.bclpbkiauv.ui.components.CloseProgressDrawable2 r3 = new im.bclpbkiauv.ui.components.CloseProgressDrawable2
                    r3.<init>()
                    r5 = r3
                    r2.setImageDrawable(r3)
                    r2 = 1088421888(0x40e00000, float:7.0)
                    int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
                    r5.setSide(r2)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    r3 = 1036831949(0x3dcccccd, float:0.1)
                    r2.setScaleX(r3)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    r2.setScaleY(r3)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    r3 = 0
                    r2.setAlpha(r3)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
                    android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
                    r3.<init>(r4, r6)
                    r2.setColorFilter(r3)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    r6 = 1108344832(0x42100000, float:36.0)
                    r7 = 1108344832(0x42100000, float:36.0)
                    r8 = 53
                    r9 = 1096810496(0x41600000, float:14.0)
                    r10 = 1093664768(0x41300000, float:11.0)
                    r11 = 1096810496(0x41600000, float:14.0)
                    r12 = 0
                    android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
                    r13.addView(r2, r3)
                    android.widget.ImageView r2 = r13.clearSearchImageView
                    im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM r3 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$7dH3WwEqPFjMVik3zq4lzttffSM
                    r3.<init>(r13)
                    r2.setOnClickListener(r3)
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField$1 r2 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField$1
                    r2.<init>(r15, r14)
                    r13.searchEditText = r2
                    r3 = 1
                    r4 = 1098907648(0x41800000, float:16.0)
                    r2.setTextSize(r3, r4)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r4 = -6774617(0xffffffffff98a0a7, float:NaN)
                    r2.setHintTextColor(r4)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r4 = -14540254(0xffffffffff222222, float:-2.1551216E38)
                    r2.setTextColor(r4)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r4 = 0
                    r2.setBackgroundDrawable(r4)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r4 = 0
                    r2.setPadding(r4, r4, r4, r4)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r2.setMaxLines(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r2.setLines(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r2.setSingleLine(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r3 = 268435459(0x10000003, float:2.5243558E-29)
                    r2.setImeOptions(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    java.lang.String r3 = "Search"
                    r4 = 2131693714(0x7f0f1092, float:1.9016564E38)
                    java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r4)
                    r2.setHint(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r3 = -11491093(0xffffffffff50a8eb, float:-2.773565E38)
                    r2.setCursorColor(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r3 = 1101004800(0x41a00000, float:20.0)
                    int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                    r2.setCursorSize(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r3 = 1069547520(0x3fc00000, float:1.5)
                    r2.setCursorWidth(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    r6 = -1082130432(0xffffffffbf800000, float:-1.0)
                    r7 = 1109393408(0x42200000, float:40.0)
                    r8 = 51
                    r9 = 1113063424(0x42580000, float:54.0)
                    r10 = 1091567616(0x41100000, float:9.0)
                    r11 = 1110966272(0x42380000, float:46.0)
                    android.widget.FrameLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
                    r13.addView(r2, r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r2 = r13.searchEditText
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField$2 r3 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField$2
                    r3.<init>(r14)
                    r2.addTextChangedListener(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r14 = r13.searchEditText
                    im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$hvDa6E2uNmv3DfBxd39ShXLqMdM r2 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchField$hvDa6E2uNmv3DfBxd39ShXLqMdM
                    r2.<init>(r13)
                    r14.setOnEditorActionListener(r2)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchField.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert, android.content.Context):void");
            }

            public /* synthetic */ void lambda$new$0$ThemeEditorView$EditorAlert$SearchField(View v) {
                this.searchEditText.setText("");
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public /* synthetic */ boolean lambda$new$1$ThemeEditorView$EditorAlert$SearchField(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    return false;
                }
                if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(this.searchEditText);
                return false;
            }

            public void hideKeyboard() {
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }

            public void showKeyboard() {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            }

            public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                super.requestDisallowInterceptTouchEvent(disallowIntercept);
            }
        }

        private class ColorPicker extends FrameLayout {
            private float alpha = 1.0f;
            private LinearGradient alphaGradient;
            private boolean alphaPressed;
            private Drawable circleDrawable;
            private Paint circlePaint;
            private boolean circlePressed;
            /* access modifiers changed from: private */
            public EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[4];
            private LinearGradient colorGradient;
            private float[] colorHSV = {0.0f, 0.0f, 1.0f};
            private boolean colorPressed;
            private Bitmap colorWheelBitmap;
            private Paint colorWheelPaint;
            private int colorWheelRadius;
            private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
            private float[] hsvTemp = new float[3];
            private LinearLayout linearLayout;
            private final int paramValueSliderWidth = AndroidUtilities.dp(20.0f);
            final /* synthetic */ EditorAlert this$1;
            private Paint valueSliderPaint;

            /* JADX WARNING: Illegal instructions before constructor call */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public ColorPicker(im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert r20, android.content.Context r21) {
                /*
                    r19 = this;
                    r0 = r19
                    r1 = r20
                    r2 = r21
                    r0.this$1 = r1
                    r0.<init>(r2)
                    r3 = 1101004800(0x41a00000, float:20.0)
                    int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                    r0.paramValueSliderWidth = r4
                    r4 = 4
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r5 = new im.bclpbkiauv.ui.components.EditTextBoldCursor[r4]
                    r0.colorEditText = r5
                    r5 = 3
                    float[] r6 = new float[r5]
                    r6 = {0, 0, 1065353216} // fill-array
                    r0.colorHSV = r6
                    r6 = 1065353216(0x3f800000, float:1.0)
                    r0.alpha = r6
                    float[] r6 = new float[r5]
                    r0.hsvTemp = r6
                    android.view.animation.DecelerateInterpolator r6 = new android.view.animation.DecelerateInterpolator
                    r6.<init>()
                    r0.decelerateInterpolator = r6
                    r6 = 0
                    r0.setWillNotDraw(r6)
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r8 = 1
                    r7.<init>(r8)
                    r0.circlePaint = r7
                    android.content.res.Resources r7 = r21.getResources()
                    r9 = 2131231193(0x7f0801d9, float:1.807846E38)
                    android.graphics.drawable.Drawable r7 = r7.getDrawable(r9)
                    android.graphics.drawable.Drawable r7 = r7.mutate()
                    r0.circleDrawable = r7
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r7.<init>()
                    r0.colorWheelPaint = r7
                    r7.setAntiAlias(r8)
                    android.graphics.Paint r7 = r0.colorWheelPaint
                    r7.setDither(r8)
                    android.graphics.Paint r7 = new android.graphics.Paint
                    r7.<init>()
                    r0.valueSliderPaint = r7
                    r7.setAntiAlias(r8)
                    android.graphics.Paint r7 = r0.valueSliderPaint
                    r7.setDither(r8)
                    android.widget.LinearLayout r7 = new android.widget.LinearLayout
                    r7.<init>(r2)
                    r0.linearLayout = r7
                    r7.setOrientation(r6)
                    android.widget.LinearLayout r7 = r0.linearLayout
                    r9 = -2
                    r10 = 49
                    android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r10)
                    r0.addView(r7, r9)
                    r7 = 0
                L_0x0081:
                    if (r7 >= r4) goto L_0x016d
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    im.bclpbkiauv.ui.components.EditTextBoldCursor r10 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
                    r10.<init>(r2)
                    r9[r7] = r10
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r10 = 2
                    r9.setInputType(r10)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = -14606047(0xffffffffff212121, float:-2.1417772E38)
                    r9.setTextColor(r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r9.setCursorColor(r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
                    r9.setCursorSize(r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 1069547520(0x3fc00000, float:1.5)
                    r9.setCursorWidth(r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 1099956224(0x41900000, float:18.0)
                    r9.setTextSize(r8, r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.createEditTextDrawable(r2, r8)
                    r9.setBackgroundDrawable(r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r9.setMaxLines(r8)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.Integer r11 = java.lang.Integer.valueOf(r7)
                    r9.setTag(r11)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    r11 = 17
                    r9.setGravity(r11)
                    if (r7 != 0) goto L_0x00f4
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "red"
                    r9.setHint(r10)
                    goto L_0x0117
                L_0x00f4:
                    if (r7 != r8) goto L_0x0100
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "green"
                    r9.setHint(r10)
                    goto L_0x0117
                L_0x0100:
                    if (r7 != r10) goto L_0x010c
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "blue"
                    r9.setHint(r10)
                    goto L_0x0117
                L_0x010c:
                    if (r7 != r5) goto L_0x0117
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    java.lang.String r10 = "alpha"
                    r9.setHint(r10)
                L_0x0117:
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r9 = r0.colorEditText
                    r9 = r9[r7]
                    if (r7 != r5) goto L_0x011f
                    r10 = 6
                    goto L_0x0120
                L_0x011f:
                    r10 = 5
                L_0x0120:
                    r11 = 268435456(0x10000000, float:2.5243549E-29)
                    r10 = r10 | r11
                    r9.setImeOptions(r10)
                    android.text.InputFilter[] r9 = new android.text.InputFilter[r8]
                    android.text.InputFilter$LengthFilter r10 = new android.text.InputFilter$LengthFilter
                    r10.<init>(r5)
                    r9[r6] = r10
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r10 = r0.colorEditText
                    r10 = r10[r7]
                    r10.setFilters(r9)
                    r10 = r7
                    android.widget.LinearLayout r11 = r0.linearLayout
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r12 = r0.colorEditText
                    r12 = r12[r7]
                    r13 = 55
                    r14 = 36
                    r15 = 0
                    r16 = 0
                    if (r7 == r5) goto L_0x0149
                    r17 = 1098907648(0x41800000, float:16.0)
                    goto L_0x014b
                L_0x0149:
                    r17 = 0
                L_0x014b:
                    r18 = 0
                    android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r13, (int) r14, (float) r15, (float) r16, (float) r17, (float) r18)
                    r11.addView(r12, r13)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r11 = r0.colorEditText
                    r11 = r11[r7]
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ColorPicker$1 r12 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ColorPicker$1
                    r12.<init>(r1, r10)
                    r11.addTextChangedListener(r12)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r11 = r0.colorEditText
                    r11 = r11[r7]
                    im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$ColorPicker$JNkBfhpIFajKcia7FAyXO-8aslA r12 = im.bclpbkiauv.ui.components.$$Lambda$ThemeEditorView$EditorAlert$ColorPicker$JNkBfhpIFajKcia7FAyXO8aslA.INSTANCE
                    r11.setOnEditorActionListener(r12)
                    int r7 = r7 + 1
                    goto L_0x0081
                L_0x016d:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.ColorPicker.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert, android.content.Context):void");
            }

            static /* synthetic */ boolean lambda$new$0(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                AndroidUtilities.hideKeyboard(textView);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                measureChild(this.linearLayout, widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(size, size);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                Canvas canvas2 = canvas;
                int centerX = (getWidth() / 2) - (this.paramValueSliderWidth * 2);
                int centerY = (getHeight() / 2) - AndroidUtilities.dp(8.0f);
                Bitmap bitmap = this.colorWheelBitmap;
                int i = this.colorWheelRadius;
                canvas2.drawBitmap(bitmap, (float) (centerX - i), (float) (centerY - i), (Paint) null);
                float hueAngle = (float) Math.toRadians((double) this.colorHSV[0]);
                int colorPointX = ((int) ((-Math.cos((double) hueAngle)) * ((double) this.colorHSV[1]) * ((double) this.colorWheelRadius))) + centerX;
                float[] fArr = this.colorHSV;
                double d = (-Math.sin((double) hueAngle)) * ((double) fArr[1]);
                int i2 = this.colorWheelRadius;
                int colorPointY = ((int) (d * ((double) i2))) + centerY;
                float f = ((float) i2) * 0.075f;
                float[] fArr2 = this.hsvTemp;
                fArr2[0] = fArr[0];
                fArr2[1] = fArr[1];
                fArr2[2] = 1.0f;
                drawPointerArrow(canvas2, colorPointX, colorPointY, Color.HSVToColor(fArr2));
                int i3 = this.colorWheelRadius;
                int x = centerX + i3 + this.paramValueSliderWidth;
                int y = centerY - i3;
                int width = AndroidUtilities.dp(9.0f);
                int height = this.colorWheelRadius * 2;
                if (this.colorGradient == null) {
                    int i4 = centerX;
                    this.colorGradient = new LinearGradient((float) x, (float) y, (float) (x + width), (float) (y + height), new int[]{-16777216, Color.HSVToColor(this.hsvTemp)}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.colorGradient);
                float f2 = (float) y;
                float f3 = (float) (y + height);
                int height2 = height;
                float f4 = (float) (x + width);
                int y2 = y;
                float f5 = f3;
                int x2 = x;
                canvas.drawRect((float) x, f2, f4, f5, this.valueSliderPaint);
                float[] fArr3 = this.colorHSV;
                drawPointerArrow(canvas2, x2 + (width / 2), (int) (((float) y2) + (fArr3[2] * ((float) height2))), Color.HSVToColor(fArr3));
                int x3 = x2 + (this.paramValueSliderWidth * 2);
                if (this.alphaGradient == null) {
                    int color = Color.HSVToColor(this.hsvTemp);
                    this.alphaGradient = new LinearGradient((float) x3, (float) y2, (float) (x3 + width), (float) (y2 + height2), new int[]{color, color & ViewCompat.MEASURED_SIZE_MASK}, (float[]) null, Shader.TileMode.CLAMP);
                }
                this.valueSliderPaint.setShader(this.alphaGradient);
                canvas.drawRect((float) x3, (float) y2, (float) (x3 + width), (float) (y2 + height2), this.valueSliderPaint);
                drawPointerArrow(canvas2, (width / 2) + x3, (int) (((float) y2) + ((1.0f - this.alpha) * ((float) height2))), (Color.HSVToColor(this.colorHSV) & ViewCompat.MEASURED_SIZE_MASK) | (((int) (this.alpha * 255.0f)) << 24));
            }

            private void drawPointerArrow(Canvas canvas, int x, int y, int color) {
                int side = AndroidUtilities.dp(13.0f);
                this.circleDrawable.setBounds(x - side, y - side, x + side, y + side);
                this.circleDrawable.draw(canvas);
                this.circlePaint.setColor(-1);
                canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(11.0f), this.circlePaint);
                this.circlePaint.setColor(color);
                canvas.drawCircle((float) x, (float) y, (float) AndroidUtilities.dp(9.0f), this.circlePaint);
            }

            /* access modifiers changed from: protected */
            public void onSizeChanged(int width, int height, int oldw, int oldh) {
                int max = Math.max(1, ((width / 2) - (this.paramValueSliderWidth * 2)) - AndroidUtilities.dp(20.0f));
                this.colorWheelRadius = max;
                this.colorWheelBitmap = createColorWheelBitmap(max * 2, max * 2);
                this.colorGradient = null;
                this.alphaGradient = null;
            }

            private Bitmap createColorWheelBitmap(int width, int height) {
                int i = width;
                int i2 = height;
                Bitmap bitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                int[] colors = new int[(12 + 1)];
                float[] hsv = {0.0f, 1.0f, 1.0f};
                for (int i3 = 0; i3 < colors.length; i3++) {
                    hsv[0] = (float) (((i3 * 30) + 180) % 360);
                    colors[i3] = Color.HSVToColor(hsv);
                }
                colors[12] = colors[0];
                this.colorWheelPaint.setShader(new ComposeShader(new SweepGradient((float) (i / 2), (float) (i2 / 2), colors, (float[]) null), new RadialGradient((float) (i / 2), (float) (i2 / 2), (float) this.colorWheelRadius, -1, ViewCompat.MEASURED_SIZE_MASK, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER));
                new Canvas(bitmap).drawCircle((float) (i / 2), (float) (i2 / 2), (float) this.colorWheelRadius, this.colorWheelPaint);
                return bitmap;
            }

            private void startColorChange(boolean start) {
                if (this.this$1.startedColorChange != start) {
                    if (this.this$1.colorChangeAnimation != null) {
                        this.this$1.colorChangeAnimation.cancel();
                    }
                    boolean unused = this.this$1.startedColorChange = start;
                    AnimatorSet unused2 = this.this$1.colorChangeAnimation = new AnimatorSet();
                    AnimatorSet access$1300 = this.this$1.colorChangeAnimation;
                    Animator[] animatorArr = new Animator[2];
                    ColorDrawable access$1400 = this.this$1.backDrawable;
                    Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                    int[] iArr = new int[1];
                    iArr[0] = start ? 0 : 51;
                    animatorArr[0] = ObjectAnimator.ofInt(access$1400, property, iArr);
                    ViewGroup access$1500 = this.this$1.containerView;
                    Property property2 = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = start ? 0.2f : 1.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(access$1500, property2, fArr);
                    access$1300.playTogether(animatorArr);
                    this.this$1.colorChangeAnimation.setDuration(150);
                    this.this$1.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
                    this.this$1.colorChangeAnimation.start();
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c4, code lost:
                if (r6 <= (r4 + r8)) goto L_0x00cc;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:3:0x000d, code lost:
                if (r1 != 2) goto L_0x0019;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:56:0x0112, code lost:
                if (r6 <= (r4 + r8)) goto L_0x0114;
             */
            /* JADX WARNING: Removed duplicated region for block: B:45:0x00f2  */
            /* JADX WARNING: Removed duplicated region for block: B:59:0x012a  */
            /* JADX WARNING: Removed duplicated region for block: B:60:0x012d  */
            /* JADX WARNING: Removed duplicated region for block: B:70:0x0143  */
            /* JADX WARNING: Removed duplicated region for block: B:71:0x0147  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onTouchEvent(android.view.MotionEvent r21) {
                /*
                    r20 = this;
                    r0 = r20
                    int r1 = r21.getAction()
                    r2 = 2
                    r3 = 0
                    r4 = 1
                    if (r1 == 0) goto L_0x001e
                    if (r1 == r4) goto L_0x0010
                    if (r1 == r2) goto L_0x001e
                    goto L_0x0019
                L_0x0010:
                    r0.alphaPressed = r3
                    r0.colorPressed = r3
                    r0.circlePressed = r3
                    r0.startColorChange(r3)
                L_0x0019:
                    boolean r2 = super.onTouchEvent(r21)
                    return r2
                L_0x001e:
                    float r5 = r21.getX()
                    int r5 = (int) r5
                    float r6 = r21.getY()
                    int r6 = (int) r6
                    int r7 = r20.getWidth()
                    int r7 = r7 / r2
                    int r8 = r0.paramValueSliderWidth
                    int r8 = r8 * 2
                    int r7 = r7 - r8
                    int r8 = r20.getHeight()
                    int r8 = r8 / r2
                    r9 = 1090519040(0x41000000, float:8.0)
                    int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                    int r8 = r8 - r9
                    int r9 = r5 - r7
                    int r10 = r6 - r8
                    int r11 = r9 * r9
                    int r12 = r10 * r10
                    int r11 = r11 + r12
                    double r11 = (double) r11
                    double r11 = java.lang.Math.sqrt(r11)
                    boolean r13 = r0.circlePressed
                    if (r13 != 0) goto L_0x0063
                    boolean r13 = r0.alphaPressed
                    if (r13 != 0) goto L_0x0060
                    boolean r13 = r0.colorPressed
                    if (r13 != 0) goto L_0x0060
                    int r13 = r0.colorWheelRadius
                    double r14 = (double) r13
                    int r13 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
                    if (r13 > 0) goto L_0x0060
                    goto L_0x0063
                L_0x0060:
                    r17 = r5
                    goto L_0x009f
                L_0x0063:
                    int r13 = r0.colorWheelRadius
                    double r14 = (double) r13
                    int r16 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
                    if (r16 <= 0) goto L_0x006b
                    double r11 = (double) r13
                L_0x006b:
                    r0.circlePressed = r4
                    float[] r13 = r0.colorHSV
                    double r14 = (double) r10
                    r17 = r5
                    double r4 = (double) r9
                    double r4 = java.lang.Math.atan2(r14, r4)
                    double r4 = java.lang.Math.toDegrees(r4)
                    r14 = 4640537203540230144(0x4066800000000000, double:180.0)
                    double r4 = r4 + r14
                    float r4 = (float) r4
                    r13[r3] = r4
                    float[] r4 = r0.colorHSV
                    int r5 = r0.colorWheelRadius
                    double r13 = (double) r5
                    double r13 = r11 / r13
                    float r5 = (float) r13
                    r13 = 1065353216(0x3f800000, float:1.0)
                    float r5 = java.lang.Math.min(r13, r5)
                    r13 = 0
                    float r5 = java.lang.Math.max(r13, r5)
                    r13 = 1
                    r4[r13] = r5
                    r4 = 0
                    r0.colorGradient = r4
                    r0.alphaGradient = r4
                L_0x009f:
                    boolean r4 = r0.colorPressed
                    r5 = 1073741824(0x40000000, float:2.0)
                    if (r4 != 0) goto L_0x00ca
                    boolean r4 = r0.circlePressed
                    if (r4 != 0) goto L_0x00c7
                    boolean r4 = r0.alphaPressed
                    if (r4 != 0) goto L_0x00c7
                    int r4 = r0.colorWheelRadius
                    int r13 = r7 + r4
                    int r14 = r0.paramValueSliderWidth
                    int r13 = r13 + r14
                    r15 = r17
                    if (r15 < r13) goto L_0x00ed
                    int r13 = r7 + r4
                    int r14 = r14 * 2
                    int r13 = r13 + r14
                    if (r15 > r13) goto L_0x00ed
                    int r13 = r8 - r4
                    if (r6 < r13) goto L_0x00ed
                    int r4 = r4 + r8
                    if (r6 > r4) goto L_0x00ed
                    goto L_0x00cc
                L_0x00c7:
                    r15 = r17
                    goto L_0x00ed
                L_0x00ca:
                    r15 = r17
                L_0x00cc:
                    int r4 = r0.colorWheelRadius
                    int r13 = r8 - r4
                    int r13 = r6 - r13
                    float r13 = (float) r13
                    float r4 = (float) r4
                    float r4 = r4 * r5
                    float r13 = r13 / r4
                    r4 = 0
                    int r14 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
                    if (r14 >= 0) goto L_0x00de
                    r13 = 0
                    goto L_0x00e6
                L_0x00de:
                    r4 = 1065353216(0x3f800000, float:1.0)
                    int r14 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
                    if (r14 <= 0) goto L_0x00e6
                    r13 = 1065353216(0x3f800000, float:1.0)
                L_0x00e6:
                    float[] r4 = r0.colorHSV
                    r4[r2] = r13
                    r4 = 1
                    r0.colorPressed = r4
                L_0x00ed:
                    boolean r4 = r0.alphaPressed
                    r13 = 4
                    if (r4 != 0) goto L_0x0114
                    boolean r4 = r0.circlePressed
                    if (r4 != 0) goto L_0x0136
                    boolean r4 = r0.colorPressed
                    if (r4 != 0) goto L_0x0136
                    int r4 = r0.colorWheelRadius
                    int r14 = r7 + r4
                    int r2 = r0.paramValueSliderWidth
                    int r18 = r2 * 3
                    int r14 = r14 + r18
                    if (r15 < r14) goto L_0x0136
                    int r14 = r7 + r4
                    int r2 = r2 * 4
                    int r14 = r14 + r2
                    if (r15 > r14) goto L_0x0136
                    int r2 = r8 - r4
                    if (r6 < r2) goto L_0x0136
                    int r4 = r4 + r8
                    if (r6 > r4) goto L_0x0136
                L_0x0114:
                    int r2 = r0.colorWheelRadius
                    int r4 = r8 - r2
                    int r4 = r6 - r4
                    float r4 = (float) r4
                    float r2 = (float) r2
                    float r2 = r2 * r5
                    float r4 = r4 / r2
                    r2 = 1065353216(0x3f800000, float:1.0)
                    float r4 = r2 - r4
                    r0.alpha = r4
                    r5 = 0
                    int r14 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                    if (r14 >= 0) goto L_0x012d
                    r0.alpha = r5
                    goto L_0x0133
                L_0x012d:
                    int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
                    if (r4 <= 0) goto L_0x0133
                    r0.alpha = r2
                L_0x0133:
                    r2 = 1
                    r0.alphaPressed = r2
                L_0x0136:
                    boolean r2 = r0.alphaPressed
                    if (r2 != 0) goto L_0x0147
                    boolean r2 = r0.colorPressed
                    if (r2 != 0) goto L_0x0147
                    boolean r2 = r0.circlePressed
                    if (r2 == 0) goto L_0x0143
                    goto L_0x0147
                L_0x0143:
                    r18 = r1
                    goto L_0x0214
                L_0x0147:
                    r2 = 1
                    r0.startColorChange(r2)
                    int r2 = r20.getColor()
                    r4 = 0
                L_0x0150:
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert r5 = r0.this$1
                    im.bclpbkiauv.ui.components.ThemeEditorView r5 = r5.this$0
                    java.util.ArrayList r5 = r5.currentThemeDesription
                    int r5 = r5.size()
                    if (r4 >= r5) goto L_0x0172
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert r5 = r0.this$1
                    im.bclpbkiauv.ui.components.ThemeEditorView r5 = r5.this$0
                    java.util.ArrayList r5 = r5.currentThemeDesription
                    java.lang.Object r5 = r5.get(r4)
                    im.bclpbkiauv.ui.actionbar.ThemeDescription r5 = (im.bclpbkiauv.ui.actionbar.ThemeDescription) r5
                    r5.setColor(r2, r3)
                    int r4 = r4 + 1
                    goto L_0x0150
                L_0x0172:
                    int r4 = android.graphics.Color.red(r2)
                    int r5 = android.graphics.Color.green(r2)
                    int r14 = android.graphics.Color.blue(r2)
                    int r13 = android.graphics.Color.alpha(r2)
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert r3 = r0.this$1
                    boolean r3 = r3.ignoreTextChange
                    if (r3 != 0) goto L_0x020d
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert r3 = r0.this$1
                    r18 = r1
                    r1 = 1
                    boolean unused = r3.ignoreTextChange = r1
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r1 = r0.colorEditText
                    r3 = 0
                    r1 = r1[r3]
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    r19 = r2
                    java.lang.String r2 = ""
                    r3.append(r2)
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    r1.setText(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r1 = r0.colorEditText
                    r3 = 1
                    r1 = r1[r3]
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    r3.append(r2)
                    r3.append(r5)
                    java.lang.String r3 = r3.toString()
                    r1.setText(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r1 = r0.colorEditText
                    r3 = 2
                    r1 = r1[r3]
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    r3.append(r2)
                    r3.append(r14)
                    java.lang.String r3 = r3.toString()
                    r1.setText(r3)
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r1 = r0.colorEditText
                    r3 = 3
                    r1 = r1[r3]
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    r3.append(r2)
                    r3.append(r13)
                    java.lang.String r2 = r3.toString()
                    r1.setText(r2)
                    r1 = 0
                L_0x01f3:
                    r2 = 4
                    if (r1 >= r2) goto L_0x0206
                    im.bclpbkiauv.ui.components.EditTextBoldCursor[] r3 = r0.colorEditText
                    r2 = r3[r1]
                    r3 = r3[r1]
                    int r3 = r3.length()
                    r2.setSelection(r3)
                    int r1 = r1 + 1
                    goto L_0x01f3
                L_0x0206:
                    im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert r1 = r0.this$1
                    r2 = 0
                    boolean unused = r1.ignoreTextChange = r2
                    goto L_0x0211
                L_0x020d:
                    r18 = r1
                    r19 = r2
                L_0x0211:
                    r20.invalidate()
                L_0x0214:
                    r1 = 1
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
            }

            public void setColor(int color) {
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int a = Color.alpha(color);
                if (!this.this$1.ignoreTextChange) {
                    boolean unused = this.this$1.ignoreTextChange = true;
                    EditTextBoldCursor editTextBoldCursor = this.colorEditText[0];
                    editTextBoldCursor.setText("" + red);
                    EditTextBoldCursor editTextBoldCursor2 = this.colorEditText[1];
                    editTextBoldCursor2.setText("" + green);
                    EditTextBoldCursor editTextBoldCursor3 = this.colorEditText[2];
                    editTextBoldCursor3.setText("" + blue);
                    EditTextBoldCursor editTextBoldCursor4 = this.colorEditText[3];
                    editTextBoldCursor4.setText("" + a);
                    for (int b = 0; b < 4; b++) {
                        EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
                        editTextBoldCursorArr[b].setSelection(editTextBoldCursorArr[b].length());
                    }
                    boolean unused2 = this.this$1.ignoreTextChange = false;
                }
                this.alphaGradient = null;
                this.colorGradient = null;
                this.alpha = ((float) a) / 255.0f;
                Color.colorToHSV(color, this.colorHSV);
                invalidate();
            }

            public int getColor() {
                return (Color.HSVToColor(this.colorHSV) & ViewCompat.MEASURED_SIZE_MASK) | (((int) (this.alpha * 255.0f)) << 24);
            }
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EditorAlert(im.bclpbkiauv.ui.components.ThemeEditorView r19, android.content.Context r20, im.bclpbkiauv.ui.actionbar.ThemeDescription[] r21) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r3 = 1
                r0.<init>(r2, r3, r3)
                r4 = 2
                android.view.View[] r5 = new android.view.View[r4]
                r0.shadow = r5
                android.animation.AnimatorSet[] r4 = new android.animation.AnimatorSet[r4]
                r0.shadowAnimation = r4
                android.content.res.Resources r4 = r20.getResources()
                r5 = 2131231573(0x7f080355, float:1.807923E38)
                android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
                android.graphics.drawable.Drawable r4 = r4.mutate()
                r0.shadowDrawable = r4
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$1 r4 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$1
                r4.<init>(r2, r1)
                r0.containerView = r4
                android.view.ViewGroup r4 = r0.containerView
                r5 = 0
                r4.setWillNotDraw(r5)
                android.view.ViewGroup r4 = r0.containerView
                int r6 = r0.backgroundPaddingLeft
                int r7 = r0.backgroundPaddingLeft
                r4.setPadding(r6, r5, r7, r5)
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                r4.<init>(r2)
                r0.frameLayout = r4
                r6 = -1
                r4.setBackgroundColor(r6)
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField r4 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchField
                r4.<init>(r2)
                r0.searchField = r4
                android.widget.FrameLayout r7 = r0.frameLayout
                r8 = 51
                android.widget.FrameLayout$LayoutParams r9 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r8)
                r7.addView(r4, r9)
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$2 r4 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$2
                r4.<init>(r2, r1)
                r0.listView = r4
                r7 = 251658240(0xf000000, float:6.3108872E-30)
                r4.setSelectorDrawableColor(r7)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r7 = 1111490560(0x42400000, float:48.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                r4.setPadding(r5, r5, r5, r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r4.setClipToPadding(r5)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                androidx.recyclerview.widget.LinearLayoutManager r9 = new androidx.recyclerview.widget.LinearLayoutManager
                android.content.Context r10 = r18.getContext()
                r9.<init>(r10)
                r0.layoutManager = r9
                r4.setLayoutManager(r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r4.setHorizontalScrollBarEnabled(r5)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r4.setVerticalScrollBarEnabled(r5)
                android.view.ViewGroup r4 = r0.containerView
                im.bclpbkiauv.ui.components.RecyclerListView r9 = r0.listView
                android.widget.FrameLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r8)
                r4.addView(r9, r10)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ListAdapter r9 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ListAdapter
                r10 = r21
                r9.<init>(r2, r10)
                r0.listAdapter = r9
                r4.setAdapter(r9)
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter r4 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter
                r4.<init>(r2)
                r0.searchAdapter = r4
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r9 = -657673(0xfffffffffff5f6f7, float:NaN)
                r4.setGlowColor(r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r9 = 0
                r4.setItemAnimator(r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                r4.setLayoutAnimation(r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$rDyC5YDVAxRnwca08U6Qj_YDKEI r9 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$rDyC5YDVAxRnwca08U6Qj_YDKEI
                r9.<init>()
                r4.setOnItemClickListener((im.bclpbkiauv.ui.components.RecyclerListView.OnItemClickListener) r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$3 r9 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$3
                r9.<init>(r1)
                r4.setOnScrollListener(r9)
                im.bclpbkiauv.ui.components.EmptyTextProgressView r4 = new im.bclpbkiauv.ui.components.EmptyTextProgressView
                r4.<init>(r2)
                r0.searchEmptyView = r4
                r4.setShowAtCenter(r3)
                im.bclpbkiauv.ui.components.EmptyTextProgressView r4 = r0.searchEmptyView
                r4.showTextView()
                im.bclpbkiauv.ui.components.EmptyTextProgressView r4 = r0.searchEmptyView
                java.lang.String r9 = "NoResult"
                r11 = 2131692244(0x7f0f0ad4, float:1.9013583E38)
                java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r11)
                r4.setText(r9)
                im.bclpbkiauv.ui.components.RecyclerListView r4 = r0.listView
                im.bclpbkiauv.ui.components.EmptyTextProgressView r9 = r0.searchEmptyView
                r4.setEmptyView(r9)
                android.view.ViewGroup r4 = r0.containerView
                im.bclpbkiauv.ui.components.EmptyTextProgressView r9 = r0.searchEmptyView
                r11 = -1082130432(0xffffffffbf800000, float:-1.0)
                r12 = -1082130432(0xffffffffbf800000, float:-1.0)
                r13 = 51
                r14 = 0
                r15 = 1112539136(0x42500000, float:52.0)
                r16 = 0
                r17 = 0
                android.widget.FrameLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r4.addView(r9, r11)
                android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
                r4.<init>(r6, r9, r8)
                r9 = 1114112000(0x42680000, float:58.0)
                int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r4.topMargin = r9
                android.view.View[] r9 = r0.shadow
                android.view.View r11 = new android.view.View
                r11.<init>(r2)
                r9[r5] = r11
                android.view.View[] r9 = r0.shadow
                r9 = r9[r5]
                r11 = 301989888(0x12000000, float:4.0389678E-28)
                r9.setBackgroundColor(r11)
                android.view.View[] r9 = r0.shadow
                r9 = r9[r5]
                r12 = 0
                r9.setAlpha(r12)
                android.view.View[] r9 = r0.shadow
                r9 = r9[r5]
                java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
                r9.setTag(r12)
                android.view.ViewGroup r9 = r0.containerView
                android.view.View[] r12 = r0.shadow
                r12 = r12[r5]
                r9.addView(r12, r4)
                android.view.ViewGroup r9 = r0.containerView
                android.widget.FrameLayout r12 = r0.frameLayout
                r13 = 58
                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r13, (int) r8)
                r9.addView(r12, r13)
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ColorPicker r9 = new im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ColorPicker
                r9.<init>(r0, r2)
                r0.colorPicker = r9
                r12 = 8
                r9.setVisibility(r12)
                android.view.ViewGroup r9 = r0.containerView
                im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$ColorPicker r13 = r0.colorPicker
                android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r3)
                r9.addView(r13, r14)
                android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.getShadowHeight()
                r14 = 83
                r9.<init>(r6, r13, r14)
                r4 = r9
                int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
                r4.bottomMargin = r7
                android.view.View[] r7 = r0.shadow
                android.view.View r9 = new android.view.View
                r9.<init>(r2)
                r7[r3] = r9
                android.view.View[] r7 = r0.shadow
                r7 = r7[r3]
                r7.setBackgroundColor(r11)
                android.view.ViewGroup r7 = r0.containerView
                android.view.View[] r9 = r0.shadow
                r9 = r9[r3]
                r7.addView(r9, r4)
                android.widget.FrameLayout r7 = new android.widget.FrameLayout
                r7.<init>(r2)
                r0.bottomSaveLayout = r7
                r7.setBackgroundColor(r6)
                android.view.ViewGroup r7 = r0.containerView
                android.widget.FrameLayout r9 = r0.bottomSaveLayout
                r11 = 48
                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r11, (int) r14)
                r7.addView(r9, r13)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r2)
                r9 = 1096810496(0x41600000, float:14.0)
                r7.setTextSize(r3, r9)
                r13 = -15095832(0xffffffffff19a7e8, float:-2.042437E38)
                r7.setTextColor(r13)
                r15 = 17
                r7.setGravity(r15)
                r11 = 788529152(0x2f000000, float:1.1641532E-10)
                android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r11, r5)
                r7.setBackgroundDrawable(r14)
                r14 = 1099956224(0x41900000, float:18.0)
                int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
                r7.setPadding(r12, r5, r11, r5)
                java.lang.String r11 = "CloseEditor"
                r12 = 2131690632(0x7f0f0488, float:1.9010313E38)
                java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r12)
                java.lang.String r11 = r11.toUpperCase()
                r7.setText(r11)
                java.lang.String r11 = "fonts/rmedium.ttf"
                android.graphics.Typeface r12 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r7.setTypeface(r12)
                android.widget.FrameLayout r12 = r0.bottomSaveLayout
                r14 = -2
                android.widget.FrameLayout$LayoutParams r5 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r8)
                r12.addView(r7, r5)
                im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$fk6a3KX1UYjvoqsPT7hVLbWwUdM r5 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$fk6a3KX1UYjvoqsPT7hVLbWwUdM
                r5.<init>()
                r7.setOnClickListener(r5)
                android.widget.TextView r5 = new android.widget.TextView
                r5.<init>(r2)
                r5.setTextSize(r3, r9)
                r5.setTextColor(r13)
                r5.setGravity(r15)
                r8 = 0
                r12 = 788529152(0x2f000000, float:1.1641532E-10)
                android.graphics.drawable.Drawable r15 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r12, r8)
                r5.setBackgroundDrawable(r15)
                r12 = 1099956224(0x41900000, float:18.0)
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                r5.setPadding(r15, r8, r13, r8)
                java.lang.String r8 = "SaveTheme"
                r12 = 2131693686(0x7f0f1076, float:1.9016507E38)
                java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r12)
                java.lang.String r8 = r8.toUpperCase()
                r5.setText(r8)
                android.graphics.Typeface r8 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r8)
                android.widget.FrameLayout r8 = r0.bottomSaveLayout
                r12 = 53
                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r12)
                r8.addView(r5, r13)
                im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$Z-QZuILvNOmh4BRM9ira2M-E3ig r8 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$Z-QZuILvNOmh4BRM9ira2M-E3ig
                r8.<init>()
                r5.setOnClickListener(r8)
                android.widget.FrameLayout r8 = new android.widget.FrameLayout
                r8.<init>(r2)
                r0.bottomLayout = r8
                r13 = 8
                r8.setVisibility(r13)
                android.widget.FrameLayout r8 = r0.bottomLayout
                r8.setBackgroundColor(r6)
                android.view.ViewGroup r8 = r0.containerView
                android.widget.FrameLayout r13 = r0.bottomLayout
                r12 = 83
                r15 = 48
                android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r6, (int) r15, (int) r12)
                r8.addView(r13, r12)
                android.widget.TextView r8 = new android.widget.TextView
                r8.<init>(r2)
                r8.setTextSize(r3, r9)
                r12 = -15095832(0xffffffffff19a7e8, float:-2.042437E38)
                r8.setTextColor(r12)
                r12 = 17
                r8.setGravity(r12)
                r12 = 788529152(0x2f000000, float:1.1641532E-10)
                r13 = 0
                android.graphics.drawable.Drawable r15 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r12, r13)
                r8.setBackgroundDrawable(r15)
                r12 = 1099956224(0x41900000, float:18.0)
                int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
                r8.setPadding(r15, r13, r3, r13)
                java.lang.String r3 = "Cancel"
                r12 = 2131690308(0x7f0f0344, float:1.9009656E38)
                java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r12)
                java.lang.String r3 = r3.toUpperCase()
                r8.setText(r3)
                android.graphics.Typeface r3 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r8.setTypeface(r3)
                android.widget.FrameLayout r3 = r0.bottomLayout
                r12 = 51
                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r12)
                r3.addView(r8, r13)
                im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$QP6P_RncNDn8MYHJaQOsnE0xNK8 r3 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$QP6P_RncNDn8MYHJaQOsnE0xNK8
                r3.<init>()
                r8.setOnClickListener(r3)
                android.widget.LinearLayout r3 = new android.widget.LinearLayout
                r3.<init>(r2)
                r12 = 0
                r3.setOrientation(r12)
                android.widget.FrameLayout r12 = r0.bottomLayout
                r13 = 53
                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r6, (int) r13)
                r12.addView(r3, r13)
                android.widget.TextView r12 = new android.widget.TextView
                r12.<init>(r2)
                r13 = 1
                r12.setTextSize(r13, r9)
                r13 = -15095832(0xffffffffff19a7e8, float:-2.042437E38)
                r12.setTextColor(r13)
                r13 = 17
                r12.setGravity(r13)
                r13 = 788529152(0x2f000000, float:1.1641532E-10)
                r15 = 0
                android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r13, r15)
                r12.setBackgroundDrawable(r9)
                r9 = 1099956224(0x41900000, float:18.0)
                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
                r12.setPadding(r13, r15, r6, r15)
                java.lang.String r6 = "Default"
                r9 = 2131690829(0x7f0f054d, float:1.9010713E38)
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r9)
                java.lang.String r6 = r6.toUpperCase()
                r12.setText(r6)
                android.graphics.Typeface r6 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r12.setTypeface(r6)
                r6 = 51
                r9 = -1
                android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r9, (int) r6)
                r3.addView(r12, r13)
                im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$0J47yqflemAMGUkrZzxtnqfb8Po r6 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$0J47yqflemAMGUkrZzxtnqfb8Po
                r6.<init>()
                r12.setOnClickListener(r6)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                r5 = r6
                r6 = 1096810496(0x41600000, float:14.0)
                r9 = 1
                r5.setTextSize(r9, r6)
                r6 = -15095832(0xffffffffff19a7e8, float:-2.042437E38)
                r5.setTextColor(r6)
                r6 = 17
                r5.setGravity(r6)
                r6 = 788529152(0x2f000000, float:1.1641532E-10)
                r9 = 0
                android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.createSelectorDrawable(r6, r9)
                r5.setBackgroundDrawable(r6)
                r6 = 1099956224(0x41900000, float:18.0)
                int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
                r5.setPadding(r13, r9, r6, r9)
                java.lang.String r6 = "Save"
                r9 = 2131693680(0x7f0f1070, float:1.9016495E38)
                java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r9)
                java.lang.String r6 = r6.toUpperCase()
                r5.setText(r6)
                android.graphics.Typeface r6 = im.bclpbkiauv.messenger.AndroidUtilities.getTypeface(r11)
                r5.setTypeface(r6)
                r6 = 51
                r9 = -1
                android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame((int) r14, (int) r9, (int) r6)
                r3.addView(r5, r6)
                im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$EY8T3SrKUuFPRFodYBAqTDEusqI r6 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$EY8T3SrKUuFPRFodYBAqTDEusqI
                r6.<init>()
                r5.setOnClickListener(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.<init>(im.bclpbkiauv.ui.components.ThemeEditorView, android.content.Context, im.bclpbkiauv.ui.actionbar.ThemeDescription[]):void");
        }

        public /* synthetic */ void lambda$new$0$ThemeEditorView$EditorAlert(View view, int position) {
            if (position != 0) {
                RecyclerView.Adapter adapter = this.listView.getAdapter();
                ListAdapter listAdapter2 = this.listAdapter;
                if (adapter == listAdapter2) {
                    ArrayList unused = this.this$0.currentThemeDesription = listAdapter2.getItem(position - 1);
                } else {
                    ArrayList unused2 = this.this$0.currentThemeDesription = this.searchAdapter.getItem(position - 1);
                }
                int unused3 = this.this$0.currentThemeDesriptionPosition = position;
                for (int a = 0; a < this.this$0.currentThemeDesription.size(); a++) {
                    ThemeDescription description = (ThemeDescription) this.this$0.currentThemeDesription.get(a);
                    if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                        this.this$0.wallpaperUpdater.showAlert(true);
                        return;
                    }
                    description.startEditing();
                    if (a == 0) {
                        this.colorPicker.setColor(description.getCurrentColor());
                    }
                }
                setColorPickerVisible(true);
            }
        }

        public /* synthetic */ void lambda$new$1$ThemeEditorView$EditorAlert(View v) {
            dismiss();
        }

        public /* synthetic */ void lambda$new$2$ThemeEditorView$EditorAlert(View v) {
            Theme.saveCurrentTheme(this.this$0.themeInfo, true, false, false);
            setOnDismissListener((DialogInterface.OnDismissListener) null);
            dismiss();
            this.this$0.close();
        }

        public /* synthetic */ void lambda$new$3$ThemeEditorView$EditorAlert(View v) {
            for (int a = 0; a < this.this$0.currentThemeDesription.size(); a++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(a)).setPreviousColor();
            }
            setColorPickerVisible(false);
        }

        public /* synthetic */ void lambda$new$4$ThemeEditorView$EditorAlert(View v) {
            for (int a = 0; a < this.this$0.currentThemeDesription.size(); a++) {
                ((ThemeDescription) this.this$0.currentThemeDesription.get(a)).setDefaultColor();
            }
            setColorPickerVisible(false);
        }

        public /* synthetic */ void lambda$new$5$ThemeEditorView$EditorAlert(View v) {
            setColorPickerVisible(false);
        }

        private void runShadowAnimation(final int num, final boolean show) {
            if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
                this.shadow[num].setTag(show ? null : 1);
                if (show) {
                    this.shadow[num].setVisibility(0);
                }
                AnimatorSet[] animatorSetArr = this.shadowAnimation;
                if (animatorSetArr[num] != null) {
                    animatorSetArr[num].cancel();
                }
                this.shadowAnimation[num] = new AnimatorSet();
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                View view = this.shadow[num];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.shadowAnimation[num].setDuration(150);
                this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (EditorAlert.this.shadowAnimation[num] != null && EditorAlert.this.shadowAnimation[num].equals(animation)) {
                            if (!show) {
                                EditorAlert.this.shadow[num].setVisibility(4);
                            }
                            EditorAlert.this.shadowAnimation[num] = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (EditorAlert.this.shadowAnimation[num] != null && EditorAlert.this.shadowAnimation[num].equals(animation)) {
                            EditorAlert.this.shadowAnimation[num] = null;
                        }
                    }
                });
                this.shadowAnimation[num].start();
            }
        }

        public void dismissInternal() {
            super.dismissInternal();
            if (this.searchField.searchEditText.isFocused()) {
                AndroidUtilities.hideKeyboard(this.searchField.searchEditText);
            }
        }

        /* access modifiers changed from: private */
        public void setColorPickerVisible(boolean visible) {
            float f = 0.0f;
            if (visible) {
                this.animationInProgress = true;
                this.colorPicker.setVisibility(0);
                this.bottomLayout.setVisibility(0);
                this.colorPicker.setAlpha(0.0f);
                this.bottomLayout.setAlpha(0.0f);
                this.previousScrollPosition = this.scrollOffsetY;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.listView.getPaddingTop()})});
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.this$0.decelerateInterpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        EditorAlert.this.listView.setVisibility(4);
                        EditorAlert.this.searchField.setVisibility(4);
                        EditorAlert.this.bottomSaveLayout.setVisibility(4);
                        boolean unused = EditorAlert.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                return;
            }
            if (this.this$0.parentActivity != null) {
                ((LaunchActivity) this.this$0.parentActivity).rebuildAllFragments(false);
            }
            Theme.saveCurrentTheme(this.this$0.themeInfo, false, false, false);
            if (this.listView.getAdapter() == this.listAdapter) {
                AndroidUtilities.hideKeyboard(getCurrentFocus());
            }
            this.animationInProgress = true;
            this.listView.setVisibility(0);
            this.bottomSaveLayout.setVisibility(0);
            this.searchField.setVisibility(0);
            this.listView.setAlpha(0.0f);
            AnimatorSet animatorSet2 = new AnimatorSet();
            Animator[] animatorArr = new Animator[8];
            animatorArr[0] = ObjectAnimator.ofFloat(this.colorPicker, View.ALPHA, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{1.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.frameLayout, View.ALPHA, new float[]{1.0f});
            View view = this.shadow[0];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (this.shadow[0].getTag() == null) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[4] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorArr[5] = ObjectAnimator.ofFloat(this.searchEmptyView, View.ALPHA, new float[]{1.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.bottomSaveLayout, View.ALPHA, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofInt(this, "scrollOffsetY", new int[]{this.previousScrollPosition});
            animatorSet2.playTogether(animatorArr);
            animatorSet2.setDuration(150);
            animatorSet2.setInterpolator(this.this$0.decelerateInterpolator);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (EditorAlert.this.listView.getAdapter() == EditorAlert.this.searchAdapter) {
                        EditorAlert.this.searchField.showKeyboard();
                    }
                    EditorAlert.this.colorPicker.setVisibility(8);
                    EditorAlert.this.bottomLayout.setVisibility(8);
                    boolean unused = EditorAlert.this.animationInProgress = false;
                }
            });
            animatorSet2.start();
            this.listView.getAdapter().notifyItemChanged(this.this$0.currentThemeDesriptionPosition);
        }

        /* access modifiers changed from: private */
        public int getCurrentTop() {
            if (this.listView.getChildCount() == 0) {
                return -1000;
            }
            int i = 0;
            View child = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
            if (holder == null) {
                return -1000;
            }
            int paddingTop = this.listView.getPaddingTop();
            if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
                i = child.getTop();
            }
            return paddingTop - i;
        }

        /* access modifiers changed from: protected */
        public boolean canDismissWithSwipe() {
            return false;
        }

        /* access modifiers changed from: private */
        public void updateLayout() {
            int top;
            int newOffset;
            if (this.listView.getChildCount() > 0 && this.listView.getVisibility() == 0 && !this.animationInProgress) {
                View child = this.listView.getChildAt(0);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
                if (this.listView.getVisibility() != 0 || this.animationInProgress) {
                    top = this.listView.getPaddingTop();
                } else {
                    top = child.getTop() - AndroidUtilities.dp(8.0f);
                }
                if (top <= (-AndroidUtilities.dp(1.0f)) || holder == null || holder.getAdapterPosition() != 0) {
                    newOffset = 0;
                    runShadowAnimation(0, true);
                } else {
                    newOffset = top;
                    runShadowAnimation(0, false);
                }
                if (this.scrollOffsetY != newOffset) {
                    setScrollOffsetY(newOffset);
                }
            }
        }

        public int getScrollOffsetY() {
            return this.scrollOffsetY;
        }

        public void setScrollOffsetY(int value) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = value;
            recyclerListView.setTopGlowOffset(value);
            this.frameLayout.setTranslationY((float) this.scrollOffsetY);
            this.colorPicker.setTranslationY((float) this.scrollOffsetY);
            this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
            this.containerView.invalidate();
        }

        public class SearchAdapter extends RecyclerListView.SelectionAdapter {
            private Context context;
            private int currentCount;
            private int lastSearchId;
            private String lastSearchText;
            private ArrayList<CharSequence> searchNames = new ArrayList<>();
            private ArrayList<ArrayList<ThemeDescription>> searchResult = new ArrayList<>();
            private Runnable searchRunnable;

            public SearchAdapter(Context context2) {
                this.context = context2;
            }

            public CharSequence generateSearchName(String name, String q) {
                if (TextUtils.isEmpty(name)) {
                    return "";
                }
                SpannableStringBuilder builder = new SpannableStringBuilder();
                String wholeString = name.trim();
                String lower = wholeString.toLowerCase();
                int lastIndex = 0;
                while (true) {
                    int indexOf = lower.indexOf(q, lastIndex);
                    int index = indexOf;
                    if (indexOf == -1) {
                        break;
                    }
                    int end = q.length() + index;
                    if (lastIndex != 0 && lastIndex != index + 1) {
                        builder.append(wholeString.substring(lastIndex, index));
                    } else if (lastIndex == 0 && index != 0) {
                        builder.append(wholeString.substring(0, index));
                    }
                    String query = wholeString.substring(index, Math.min(wholeString.length(), end));
                    if (query.startsWith(" ")) {
                        builder.append(" ");
                    }
                    String query2 = query.trim();
                    int start = builder.length();
                    builder.append(query2);
                    builder.setSpan(new ForegroundColorSpan(-11697229), start, query2.length() + start, 33);
                    lastIndex = end;
                }
                if (lastIndex != -1 && lastIndex < wholeString.length()) {
                    builder.append(wholeString.substring(lastIndex));
                }
                return builder;
            }

            /* access modifiers changed from: private */
            /* renamed from: searchDialogsInternal */
            public void lambda$searchDialogs$1$ThemeEditorView$EditorAlert$SearchAdapter(String query, int searchId) {
                try {
                    String search1 = query.trim().toLowerCase();
                    if (search1.length() == 0) {
                        this.lastSearchId = -1;
                        updateSearchResults(new ArrayList(), new ArrayList(), this.lastSearchId);
                        return;
                    }
                    String search2 = LocaleController.getInstance().getTranslitString(search1);
                    if (search1.equals(search2) || search2.length() == 0) {
                        search2 = null;
                    }
                    String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                    search[0] = search1;
                    if (search2 != null) {
                        search[1] = search2;
                    }
                    ArrayList<ArrayList<ThemeDescription>> searchResults = new ArrayList<>();
                    ArrayList<CharSequence> names = new ArrayList<>();
                    int N = EditorAlert.this.listAdapter.items.size();
                    for (int a = 0; a < N; a++) {
                        ArrayList<ThemeDescription> themeDescriptions = (ArrayList) EditorAlert.this.listAdapter.items.get(a);
                        String key = themeDescriptions.get(0).getCurrentKey();
                        String name = key.toLowerCase();
                        int length = search.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            String q = search[i];
                            if (name.contains(q)) {
                                searchResults.add(themeDescriptions);
                                names.add(generateSearchName(key, q));
                                break;
                            }
                            i++;
                        }
                    }
                    try {
                        updateSearchResults(searchResults, names, searchId);
                    } catch (Exception e) {
                        e = e;
                    }
                } catch (Exception e2) {
                    e = e2;
                    int i2 = searchId;
                    FileLog.e((Throwable) e);
                }
            }

            private void updateSearchResults(ArrayList<ArrayList<ThemeDescription>> result, ArrayList<CharSequence> names, int searchId) {
                AndroidUtilities.runOnUIThread(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                      (wrap: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw) = 
                      (r1v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                      (r4v0 'searchId' int)
                      (r2v0 'result' java.util.ArrayList<java.util.ArrayList<im.bclpbkiauv.ui.actionbar.ThemeDescription>>)
                      (r3v0 'names' java.util.ArrayList<java.lang.CharSequence>)
                     call: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter, int, java.util.ArrayList, java.util.ArrayList):void type: CONSTRUCTOR)
                     im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.updateSearchResults(java.util.ArrayList, java.util.ArrayList, int):void, dex: classes6.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw) = 
                      (r1v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                      (r4v0 'searchId' int)
                      (r2v0 'result' java.util.ArrayList<java.util.ArrayList<im.bclpbkiauv.ui.actionbar.ThemeDescription>>)
                      (r3v0 'names' java.util.ArrayList<java.lang.CharSequence>)
                     call: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter, int, java.util.ArrayList, java.util.ArrayList):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.updateSearchResults(java.util.ArrayList, java.util.ArrayList, int):void, dex: classes6.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw r0 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$ukbERPnMZiSOpjbw6eauF7GVhkw
                    r0.<init>(r1, r4, r2, r3)
                    im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.updateSearchResults(java.util.ArrayList, java.util.ArrayList, int):void");
            }

            public /* synthetic */ void lambda$updateSearchResults$0$ThemeEditorView$EditorAlert$SearchAdapter(int searchId, ArrayList result, ArrayList names) {
                if (searchId == this.lastSearchId) {
                    if (EditorAlert.this.listView.getAdapter() != EditorAlert.this.searchAdapter) {
                        EditorAlert editorAlert = EditorAlert.this;
                        int unused = editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        EditorAlert.this.listView.setAdapter(EditorAlert.this.searchAdapter);
                        EditorAlert.this.searchAdapter.notifyDataSetChanged();
                    }
                    boolean isEmpty = true;
                    boolean becomeEmpty = !this.searchResult.isEmpty() && result.isEmpty();
                    if (!this.searchResult.isEmpty() || !result.isEmpty()) {
                        isEmpty = false;
                    }
                    if (becomeEmpty) {
                        EditorAlert editorAlert2 = EditorAlert.this;
                        int unused2 = editorAlert2.topBeforeSwitch = editorAlert2.getCurrentTop();
                    }
                    this.searchResult = result;
                    this.searchNames = names;
                    notifyDataSetChanged();
                    if (!isEmpty && !becomeEmpty && EditorAlert.this.topBeforeSwitch > 0) {
                        EditorAlert.this.layoutManager.scrollToPositionWithOffset(0, -EditorAlert.this.topBeforeSwitch);
                        int unused3 = EditorAlert.this.topBeforeSwitch = -1000;
                    }
                    EditorAlert.this.searchEmptyView.showTextView();
                }
            }

            public void searchDialogs(String query) {
                if (query == null || !query.equals(this.lastSearchText)) {
                    this.lastSearchText = query;
                    if (this.searchRunnable != null) {
                        Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                        this.searchRunnable = null;
                    }
                    if (query == null || query.length() == 0) {
                        this.searchResult.clear();
                        EditorAlert editorAlert = EditorAlert.this;
                        int unused = editorAlert.topBeforeSwitch = editorAlert.getCurrentTop();
                        this.lastSearchId = -1;
                        notifyDataSetChanged();
                        return;
                    }
                    int searchId = this.lastSearchId + 1;
                    this.lastSearchId = searchId;
                    this.searchRunnable = 
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002f: IPUT  
                          (wrap: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0 : 0x002c: CONSTRUCTOR  (r1v1 im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0) = 
                          (r5v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                          (r6v0 'query' java.lang.String)
                          (r0v6 'searchId' int)
                         call: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter, java.lang.String, int):void type: CONSTRUCTOR)
                          (r5v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                         im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.searchRunnable java.lang.Runnable in method: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.searchDialogs(java.lang.String):void, dex: classes6.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002c: CONSTRUCTOR  (r1v1 im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0) = 
                          (r5v0 'this' im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter A[THIS])
                          (r6v0 'query' java.lang.String)
                          (r0v6 'searchId' int)
                         call: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0.<init>(im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert$SearchAdapter, java.lang.String, int):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.searchDialogs(java.lang.String):void, dex: classes6.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 70 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 74 more
                        */
                    /*
                        this = this;
                        if (r6 == 0) goto L_0x000b
                        java.lang.String r0 = r5.lastSearchText
                        boolean r0 = r6.equals(r0)
                        if (r0 == 0) goto L_0x000b
                        return
                    L_0x000b:
                        r5.lastSearchText = r6
                        java.lang.Runnable r0 = r5.searchRunnable
                        if (r0 == 0) goto L_0x001b
                        im.bclpbkiauv.messenger.DispatchQueue r0 = im.bclpbkiauv.messenger.Utilities.searchQueue
                        java.lang.Runnable r1 = r5.searchRunnable
                        r0.cancelRunnable(r1)
                        r0 = 0
                        r5.searchRunnable = r0
                    L_0x001b:
                        if (r6 == 0) goto L_0x003b
                        int r0 = r6.length()
                        if (r0 != 0) goto L_0x0024
                        goto L_0x003b
                    L_0x0024:
                        int r0 = r5.lastSearchId
                        int r0 = r0 + 1
                        r5.lastSearchId = r0
                        im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0 r1 = new im.bclpbkiauv.ui.components.-$$Lambda$ThemeEditorView$EditorAlert$SearchAdapter$T6kiP3MzBQNm8DHDK3xFJbDLoU0
                        r1.<init>(r5, r6, r0)
                        r5.searchRunnable = r1
                        im.bclpbkiauv.messenger.DispatchQueue r1 = im.bclpbkiauv.messenger.Utilities.searchQueue
                        java.lang.Runnable r2 = r5.searchRunnable
                        r3 = 300(0x12c, double:1.48E-321)
                        r1.postRunnable(r2, r3)
                        goto L_0x004f
                    L_0x003b:
                        java.util.ArrayList<java.util.ArrayList<im.bclpbkiauv.ui.actionbar.ThemeDescription>> r0 = r5.searchResult
                        r0.clear()
                        im.bclpbkiauv.ui.components.ThemeEditorView$EditorAlert r0 = im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.this
                        int r1 = r0.getCurrentTop()
                        int unused = r0.topBeforeSwitch = r1
                        r0 = -1
                        r5.lastSearchId = r0
                        r5.notifyDataSetChanged()
                    L_0x004f:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.ThemeEditorView.EditorAlert.SearchAdapter.searchDialogs(java.lang.String):void");
                }

                public int getItemCount() {
                    if (this.searchResult.isEmpty()) {
                        return 0;
                    }
                    return this.searchResult.size() + 1;
                }

                public ArrayList<ThemeDescription> getItem(int i) {
                    if (i < 0 || i >= this.searchResult.size()) {
                        return null;
                    }
                    return this.searchResult.get(i);
                }

                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return true;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType != 0) {
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    } else {
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    int color;
                    if (holder.getItemViewType() == 0) {
                        ThemeDescription description = this.searchResult.get(position - 1).get(0);
                        if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                            color = 0;
                        } else {
                            color = description.getSetColor();
                        }
                        ((TextColorThemeCell) holder.itemView).setTextAndColor(this.searchNames.get(position - 1), color);
                    }
                }

                public int getItemViewType(int i) {
                    if (i == 0) {
                        return 1;
                    }
                    return 0;
                }
            }

            private class ListAdapter extends RecyclerListView.SelectionAdapter {
                private Context context;
                private int currentCount;
                /* access modifiers changed from: private */
                public ArrayList<ArrayList<ThemeDescription>> items = new ArrayList<>();

                public ListAdapter(Context context2, ThemeDescription[] descriptions) {
                    this.context = context2;
                    HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap<>();
                    for (ThemeDescription description : descriptions) {
                        String key = description.getCurrentKey();
                        ArrayList<ThemeDescription> arrayList = itemsMap.get(key);
                        if (arrayList == null) {
                            arrayList = new ArrayList<>();
                            itemsMap.put(key, arrayList);
                            this.items.add(arrayList);
                        }
                        arrayList.add(description);
                    }
                }

                public int getItemCount() {
                    if (this.items.isEmpty()) {
                        return 0;
                    }
                    return this.items.size() + 1;
                }

                public ArrayList<ThemeDescription> getItem(int i) {
                    if (i < 0 || i >= this.items.size()) {
                        return null;
                    }
                    return this.items.get(i);
                }

                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    return true;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType != 0) {
                        view = new View(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
                    } else {
                        view = new TextColorThemeCell(this.context);
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    int color;
                    if (holder.getItemViewType() == 0) {
                        ThemeDescription description = this.items.get(position - 1).get(0);
                        if (description.getCurrentKey().equals(Theme.key_chat_wallpaper)) {
                            color = 0;
                        } else {
                            color = description.getSetColor();
                        }
                        ((TextColorThemeCell) holder.itemView).setTextAndColor(description.getTitle(), color);
                    }
                }

                public int getItemViewType(int i) {
                    if (i == 0) {
                        return 1;
                    }
                    return 0;
                }
            }
        }

        public void show(Activity activity, Theme.ThemeInfo theme) {
            if (Instance != null) {
                Instance.destroy();
            }
            this.hidden = false;
            this.themeInfo = theme;
            AnonymousClass1 r1 = new FrameLayout(activity) {
                private boolean dragging;
                private float startX;
                private float startY;

                public boolean onInterceptTouchEvent(MotionEvent event) {
                    return true;
                }

                public boolean onTouchEvent(MotionEvent event) {
                    BaseFragment fragment;
                    ThemeDescription[] items;
                    float x = event.getRawX();
                    float y = event.getRawY();
                    if (event.getAction() == 0) {
                        this.startX = x;
                        this.startY = y;
                    } else if (event.getAction() != 2 || this.dragging) {
                        if (event.getAction() == 1 && !this.dragging && ThemeEditorView.this.editorAlert == null) {
                            LaunchActivity launchActivity = (LaunchActivity) ThemeEditorView.this.parentActivity;
                            ActionBarLayout actionBarLayout = null;
                            if (AndroidUtilities.isTablet()) {
                                actionBarLayout = launchActivity.getLayersActionBarLayout();
                                if (actionBarLayout != null && actionBarLayout.fragmentsStack.isEmpty()) {
                                    actionBarLayout = null;
                                }
                                if (actionBarLayout == null && (actionBarLayout = launchActivity.getRightActionBarLayout()) != null && actionBarLayout.fragmentsStack.isEmpty()) {
                                    actionBarLayout = null;
                                }
                            }
                            if (actionBarLayout == null) {
                                actionBarLayout = launchActivity.getActionBarLayout();
                            }
                            if (actionBarLayout != null) {
                                if (!actionBarLayout.fragmentsStack.isEmpty()) {
                                    fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
                                } else {
                                    fragment = null;
                                }
                                if (!(fragment == null || (items = fragment.getThemeDescriptions()) == null)) {
                                    ThemeEditorView themeEditorView = ThemeEditorView.this;
                                    ThemeEditorView themeEditorView2 = ThemeEditorView.this;
                                    EditorAlert unused = themeEditorView.editorAlert = new EditorAlert(themeEditorView2, themeEditorView2.parentActivity, items);
                                    ThemeEditorView.this.editorAlert.setOnDismissListener($$Lambda$ThemeEditorView$1$dkrwyqj3q4G7FjtqL5Vdb1ZVfPI.INSTANCE);
                                    ThemeEditorView.this.editorAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        public final void onDismiss(DialogInterface dialogInterface) {
                                            ThemeEditorView.AnonymousClass1.this.lambda$onTouchEvent$1$ThemeEditorView$1(dialogInterface);
                                        }
                                    });
                                    ThemeEditorView.this.editorAlert.show();
                                    ThemeEditorView.this.hide();
                                }
                            }
                        }
                    } else if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.3f, true) || Math.abs(this.startY - y) >= AndroidUtilities.getPixelsInCM(0.3f, false)) {
                        this.dragging = true;
                        this.startX = x;
                        this.startY = y;
                    }
                    if (this.dragging) {
                        if (event.getAction() == 2) {
                            WindowManager.LayoutParams access$5300 = ThemeEditorView.this.windowLayoutParams;
                            access$5300.x = (int) (((float) access$5300.x) + (x - this.startX));
                            WindowManager.LayoutParams access$53002 = ThemeEditorView.this.windowLayoutParams;
                            access$53002.y = (int) (((float) access$53002.y) + (y - this.startY));
                            int maxDiff = ThemeEditorView.this.editorWidth / 2;
                            if (ThemeEditorView.this.windowLayoutParams.x < (-maxDiff)) {
                                ThemeEditorView.this.windowLayoutParams.x = -maxDiff;
                            } else if (ThemeEditorView.this.windowLayoutParams.x > (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff) {
                                ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) + maxDiff;
                            }
                            float alpha = 1.0f;
                            if (ThemeEditorView.this.windowLayoutParams.x < 0) {
                                alpha = ((((float) ThemeEditorView.this.windowLayoutParams.x) / ((float) maxDiff)) * 0.5f) + 1.0f;
                            } else if (ThemeEditorView.this.windowLayoutParams.x > AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) {
                                alpha = 1.0f - ((((float) ((ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x) + ThemeEditorView.this.windowLayoutParams.width)) / ((float) maxDiff)) * 0.5f);
                            }
                            if (ThemeEditorView.this.windowView.getAlpha() != alpha) {
                                ThemeEditorView.this.windowView.setAlpha(alpha);
                            }
                            if (ThemeEditorView.this.windowLayoutParams.y < (-0)) {
                                ThemeEditorView.this.windowLayoutParams.y = -0;
                            } else if (ThemeEditorView.this.windowLayoutParams.y > (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0) {
                                ThemeEditorView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height) + 0;
                            }
                            ThemeEditorView.this.windowManager.updateViewLayout(ThemeEditorView.this.windowView, ThemeEditorView.this.windowLayoutParams);
                            this.startX = x;
                            this.startY = y;
                        } else if (event.getAction() == 1) {
                            this.dragging = false;
                            ThemeEditorView.this.animateToBoundsMaybe();
                        }
                    }
                    return true;
                }

                static /* synthetic */ void lambda$onTouchEvent$0(DialogInterface dialog) {
                }

                public /* synthetic */ void lambda$onTouchEvent$1$ThemeEditorView$1(DialogInterface dialog) {
                    EditorAlert unused = ThemeEditorView.this.editorAlert = null;
                    ThemeEditorView.this.show();
                }
            };
            this.windowView = r1;
            r1.setBackgroundResource(R.drawable.theme_picker);
            this.windowManager = (WindowManager) activity.getSystemService("window");
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            this.preferences = sharedPreferences;
            int sidex = sharedPreferences.getInt("sidex", 1);
            int sidey = this.preferences.getInt("sidey", 0);
            float px = this.preferences.getFloat("px", 0.0f);
            float py = this.preferences.getFloat("py", 0.0f);
            try {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                this.windowLayoutParams = layoutParams;
                layoutParams.width = this.editorWidth;
                this.windowLayoutParams.height = this.editorHeight;
                this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
                this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
                this.windowLayoutParams.format = -3;
                this.windowLayoutParams.gravity = 51;
                this.windowLayoutParams.type = 99;
                this.windowLayoutParams.flags = 16777736;
                this.windowManager.addView(this.windowView, this.windowLayoutParams);
                this.wallpaperUpdater = new WallpaperUpdater(activity, (BaseFragment) null, new WallpaperUpdater.WallpaperUpdaterDelegate() {
                    public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                        Theme.setThemeWallpaper(ThemeEditorView.this.themeInfo, bitmap, file);
                    }

                    public void needOpenColorPicker() {
                        for (int a = 0; a < ThemeEditorView.this.currentThemeDesription.size(); a++) {
                            ThemeDescription description = (ThemeDescription) ThemeEditorView.this.currentThemeDesription.get(a);
                            description.startEditing();
                            if (a == 0) {
                                ThemeEditorView.this.editorAlert.colorPicker.setColor(description.getCurrentColor());
                            }
                        }
                        ThemeEditorView.this.editorAlert.setColorPickerVisible(true);
                    }
                });
                Instance = this;
                this.parentActivity = activity;
                showWithAnimation();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        private void showWithAnimation() {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[]{0.0f, 1.0f})});
            animatorSet.setInterpolator(this.decelerateInterpolator);
            animatorSet.setDuration(150);
            animatorSet.start();
        }

        private static int getSideCoord(boolean isX, int side, float p, int sideSize) {
            int total;
            int result;
            if (isX) {
                total = AndroidUtilities.displaySize.x - sideSize;
            } else {
                total = (AndroidUtilities.displaySize.y - sideSize) - ActionBar.getCurrentActionBarHeight();
            }
            if (side == 0) {
                result = AndroidUtilities.dp(10.0f);
            } else if (side == 1) {
                result = total - AndroidUtilities.dp(10.0f);
            } else {
                result = AndroidUtilities.dp(10.0f) + Math.round(((float) (total - AndroidUtilities.dp(20.0f))) * p);
            }
            if (!isX) {
                return result + ActionBar.getCurrentActionBarHeight();
            }
            return result;
        }

        /* access modifiers changed from: private */
        public void hide() {
            if (this.parentActivity != null) {
                try {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[]{1.0f, 0.0f})});
                    animatorSet.setInterpolator(this.decelerateInterpolator);
                    animatorSet.setDuration(150);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ThemeEditorView.this.windowView != null) {
                                ThemeEditorView.this.windowManager.removeView(ThemeEditorView.this.windowView);
                            }
                        }
                    });
                    animatorSet.start();
                    this.hidden = true;
                } catch (Exception e) {
                }
            }
        }

        /* access modifiers changed from: private */
        public void show() {
            if (this.parentActivity != null) {
                try {
                    this.windowManager.addView(this.windowView, this.windowLayoutParams);
                    this.hidden = false;
                    showWithAnimation();
                } catch (Exception e) {
                }
            }
        }

        public void close() {
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Exception e) {
            }
            this.parentActivity = null;
        }

        public void onConfigurationChanged() {
            int sidex = this.preferences.getInt("sidex", 1);
            int sidey = this.preferences.getInt("sidey", 0);
            float px = this.preferences.getFloat("px", 0.0f);
            float py = this.preferences.getFloat("py", 0.0f);
            this.windowLayoutParams.x = getSideCoord(true, sidex, px, this.editorWidth);
            this.windowLayoutParams.y = getSideCoord(false, sidey, py, this.editorHeight);
            try {
                if (this.windowView.getParent() != null) {
                    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            WallpaperUpdater wallpaperUpdater2 = this.wallpaperUpdater;
            if (wallpaperUpdater2 != null) {
                wallpaperUpdater2.onActivityResult(requestCode, resultCode, data);
            }
        }

        /* access modifiers changed from: private */
        public void animateToBoundsMaybe() {
            int startX = getSideCoord(true, 0, 0.0f, this.editorWidth);
            int endX = getSideCoord(true, 1, 0.0f, this.editorWidth);
            int startY = getSideCoord(false, 0, 0.0f, this.editorHeight);
            int endY = getSideCoord(false, 1, 0.0f, this.editorHeight);
            ArrayList<Animator> animators = null;
            SharedPreferences.Editor editor = this.preferences.edit();
            int maxDiff = AndroidUtilities.dp(20.0f);
            boolean slideOut = false;
            if (Math.abs(startX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x < 0 && this.windowLayoutParams.x > (-this.editorWidth) / 4)) {
                if (0 == 0) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidex", 0);
                if (this.windowView.getAlpha() != 1.0f) {
                    animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}));
                }
                animators.add(ObjectAnimator.ofInt(this, "x", new int[]{startX}));
            } else if (Math.abs(endX - this.windowLayoutParams.x) <= maxDiff || (this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.editorWidth && this.windowLayoutParams.x < AndroidUtilities.displaySize.x - ((this.editorWidth / 4) * 3))) {
                if (0 == 0) {
                    animators = new ArrayList<>();
                }
                editor.putInt("sidex", 1);
                if (this.windowView.getAlpha() != 1.0f) {
                    animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}));
                }
                animators.add(ObjectAnimator.ofInt(this, "x", new int[]{endX}));
            } else if (this.windowView.getAlpha() != 1.0f) {
                if (0 == 0) {
                    animators = new ArrayList<>();
                }
                if (this.windowLayoutParams.x < 0) {
                    animators.add(ObjectAnimator.ofInt(this, "x", new int[]{-this.editorWidth}));
                } else {
                    animators.add(ObjectAnimator.ofInt(this, "x", new int[]{AndroidUtilities.displaySize.x}));
                }
                slideOut = true;
            } else {
                editor.putFloat("px", ((float) (this.windowLayoutParams.x - startX)) / ((float) (endX - startX)));
                editor.putInt("sidex", 2);
            }
            if (!slideOut) {
                if (Math.abs(startY - this.windowLayoutParams.y) <= maxDiff || this.windowLayoutParams.y <= ActionBar.getCurrentActionBarHeight()) {
                    if (animators == null) {
                        animators = new ArrayList<>();
                    }
                    editor.putInt("sidey", 0);
                    animators.add(ObjectAnimator.ofInt(this, "y", new int[]{startY}));
                } else if (Math.abs(endY - this.windowLayoutParams.y) <= maxDiff) {
                    if (animators == null) {
                        animators = new ArrayList<>();
                    }
                    editor.putInt("sidey", 1);
                    animators.add(ObjectAnimator.ofInt(this, "y", new int[]{endY}));
                } else {
                    editor.putFloat("py", ((float) (this.windowLayoutParams.y - startY)) / ((float) (endY - startY)));
                    editor.putInt("sidey", 2);
                }
                editor.commit();
            }
            if (animators != null) {
                if (this.decelerateInterpolator == null) {
                    this.decelerateInterpolator = new DecelerateInterpolator();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setInterpolator(this.decelerateInterpolator);
                animatorSet.setDuration(150);
                if (slideOut) {
                    animators.add(ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f}));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            Theme.saveCurrentTheme(ThemeEditorView.this.themeInfo, true, false, false);
                            ThemeEditorView.this.destroy();
                        }
                    });
                }
                animatorSet.playTogether(animators);
                animatorSet.start();
            }
        }

        public int getX() {
            return this.windowLayoutParams.x;
        }

        public int getY() {
            return this.windowLayoutParams.y;
        }

        public void setX(int value) {
            this.windowLayoutParams.x = value;
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }

        public void setY(int value) {
            this.windowLayoutParams.y = value;
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }
