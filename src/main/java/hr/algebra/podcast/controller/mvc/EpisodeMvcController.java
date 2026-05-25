package hr.algebra.podcast.controller.mvc;

import hr.algebra.podcast.dto.EpisodeDto;
import hr.algebra.podcast.entity.User;
import hr.algebra.podcast.enums.ListeningContext;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PlaybackSpeed;
import hr.algebra.podcast.enums.PodcastCategory;
import hr.algebra.podcast.service.EpisodeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/episodes")
public class EpisodeMvcController {

    private final EpisodeService episodeService;

    public EpisodeMvcController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @GetMapping
    public String list(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) PodcastCategory category,
        @RequestParam(required = false) ListeningStatus status,
        @RequestParam(defaultValue = "false") boolean subscribedOnly,
        Model model
    ) {
        boolean searching = query != null || category != null || status != null || subscribedOnly;
        model.addAttribute("episodes", searching
            ? episodeService.search(query, category, status, subscribedOnly)
            : episodeService.findAll());
        addEnumsToModel(model);
        model.addAttribute("query", query);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("subscribedOnly", subscribedOnly);
        return "episodes/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("episode", episodeService.findById(id));
            return "episodes/detail";
        } catch (NoSuchElementException e) {
            return "redirect:/episodes";
        }
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("episode", new EpisodeDto(
            null, "", "", "", "", "", "", null,
            null, null, null, null,
            null, null, null, null, null, null, null,
            false, false, false, false,
            null, null, null,
            "", "", "", "", "", "",
            null, null, null
        ));
        addEnumsToModel(model);
        model.addAttribute("editMode", false);
        return "episodes/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(
        @Valid @ModelAttribute("episode") EpisodeDto dto,
        BindingResult result,
        @AuthenticationPrincipal User currentUser,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            addEnumsToModel(model);
            model.addAttribute("editMode", false);
            return "episodes/form";
        }
        episodeService.create(dto, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Episode added to your queue.");
        return "redirect:/episodes";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("episode", episodeService.findById(id));
            addEnumsToModel(model);
            model.addAttribute("editMode", true);
            return "episodes/form";
        } catch (NoSuchElementException e) {
            return "redirect:/episodes";
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(
        @PathVariable Long id,
        @Valid @ModelAttribute("episode") EpisodeDto dto,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            addEnumsToModel(model);
            model.addAttribute("editMode", true);
            return "episodes/form";
        }
        episodeService.update(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Episode updated.");
        return "redirect:/episodes";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        episodeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Episode removed.");
        return "redirect:/episodes";
    }

    private void addEnumsToModel(Model model) {
        model.addAttribute("categories", PodcastCategory.values());
        model.addAttribute("statuses", ListeningStatus.values());
        model.addAttribute("contexts", ListeningContext.values());
        model.addAttribute("speeds", PlaybackSpeed.values());
    }
}
