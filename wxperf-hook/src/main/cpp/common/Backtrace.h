//
// Created by Yves on 2019-08-09.
//

#ifndef LIBWXPERF_JNI_BACKTRACE_H
#define LIBWXPERF_JNI_BACKTRACE_H

#include <unwindstack/Maps.h>
#include <unwindstack/Regs.h>
#include <unwindstack/RegsArm64.h>
#include <unwindstack/RegsGetLocal.h>
#include <unwindstack/Unwinder.h>
#include <unwindstack/JitDebug.h>
#include <FpUnwinder.h>
#include "Log.h"
#include "Predefined.h"

#define WECHAT_BACKTRACE_TAG "Wechat.Backtrace"

namespace wechat_backtrace {

    void update_maps();

    void dwarf_unwind(unwindstack::Regs* regs, std::vector<unwindstack::FrameData> &, size_t);

    void fp_unwind(uptr *regs, uptr *frames, uptr frameMaxSize, uptr &frameSize);

    void quicken_unwind(uptr* regs, uptr* frames, uptr frame_max_size, uptr &frame_size);

    void quicken_unwind_v2_wip(uptr* regs, uptr* frames, uptr frame_max_size, uptr &frame_size);

    void fp_unwind_with_fallback(uptr* regs, uptr* frames, uptr frameMaxSize, uptr &frameSize);

#ifdef __arm__
    void fast_dwarf_unwind(uptr* regs, uptr* frames, uptr frame_max_size, uptr &frame_size);
#else
    void fast_dwarf_unwind(unwindstack::Regs* regs, std::vector<unwindstack::FrameData> &dst, size_t frameSize);
#endif
}

#endif //LIBWXPERF_JNI_BACKTRACE_H
