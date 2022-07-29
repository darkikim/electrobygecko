package electro.by.gecko.vitrine.controller.admin;

import electro.by.gecko.vitrine.entity.Product;
import electro.by.gecko.vitrine.service.file.StorageService;
import electro.by.gecko.vitrine.service.product.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Controller
@RequestMapping(path = "/admin/products")
public class AdminProductController {

    private final ProductService productDAOService;

    private final StorageService storageService;

    public AdminProductController(ProductService productDAOService, StorageService storageService) {
        this.productDAOService = productDAOService;
        this.storageService = storageService;
    }


    @GetMapping(path = "/list")
    public String list(Model model) {

        model.addAttribute("products", productDAOService.findAll());

        return "admin/products/list";
    }

    @GetMapping(path = "/new")
    public String formCreate(Model model) {
        model.addAttribute("product", new Product());

        return "admin/products/new";
    }

    @GetMapping(path = "/{id}")
    public String formEdit(Model model, @PathVariable Long id) {
        model.addAttribute("product", productDAOService.findById(id));

        return "admin/products/edit";
    }

    @PostMapping
    public String save(Product product,@RequestParam("file") MultipartFile image) {
        if (product.getId() == null) {
            product.setAddedDate(LocalDate.now());
        }
        if(null != image && !image.isEmpty()) {
            if(null != product.getImage() && !product.getImage().isEmpty() && !product.getImage().startsWith("images/")){
                storageService.load(product.getImage()).toFile().delete();
            }
            product.setImage(storageService.store(image));
        }
        productDAOService.save(product);

        return "redirect:/admin/products/list";
    }
}
