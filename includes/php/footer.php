<?php
	
	defined('secured') or header("Location: https://manager.badblock.fr/");

?>

                    </div>

                </div>

                <footer class="footer text-right">
                    &copy; 2013 - <?php echo date("Y"); ?>. Tous droits réservés.
                </footer>

            </div>

        </div>
		
        <script>
            var resizefunc = [];
        </script>
		
        <script src="/assets/js/jquery.min.js"></script>
        <script src="/assets/js/popper.min.js"></script>
        <script src="/assets/js/bootstrap.min.js"></script>
        <script src="/assets/js/detect.js"></script>
        <script src="/assets/js/fastclick.js"></script>
        <script src="/assets/js/jquery.slimscroll.js"></script>
        <script src="/assets/js/jquery.blockUI.js"></script>
        <script src="/assets/js/waves.js"></script>
        <script src="/assets/js/wow.min.js"></script>
        <script src="/assets/js/jquery.nicescroll.js"></script>
        <script src="/assets/js/jquery.scrollTo.min.js"></script>

		<?php
		
		if (!isset($removeJs) || !$removeJs)
		{
			
		?>
			
        <script src="/plugins/bootstrap-table/js/bootstrap-table.js"></script>

        <script src="/assets/pages/jquery.bs-table.js"></script>

		<script src="/plugins/bootstrap-tagsinput/js/bootstrap-tagsinput.min.js"></script>
        <script src="/plugins/switchery/js/switchery.min.js"></script>
        <script type="text/javascript" src="/plugins/multiselect/js/jquery.multi-select.js"></script>
        <script type="text/javascript" src="/plugins/jquery-quicksearch/jquery.quicksearch.js"></script>
        <script src="/plugins/select2/js/select2.min.js" type="text/javascript"></script>
        <script src="/plugins/bootstrap-select/js/bootstrap-select.min.js" type="text/javascript"></script>
        <script src="/plugins/bootstrap-filestyle/js/bootstrap-filestyle.min.js" type="text/javascript"></script>
        <script src="/plugins/bootstrap-touchspin/js/jquery.bootstrap-touchspin.min.js" type="text/javascript"></script>
        <script src="/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js" type="text/javascript"></script>

        <script type="text/javascript" src="/plugins/autocomplete/jquery.mockjax.js"></script>
        <script type="text/javascript" src="/plugins/autocomplete/jquery.autocomplete.min.js"></script>
        <script type="text/javascript" src="/plugins/autocomplete/countries.js"></script>
        <script type="text/javascript" src="/assets/pages/autocomplete.js"></script>

        <script type="text/javascript" src="/assets/pages/jquery.form-advanced.init.js"></script>
		
		<script src="/assets/pages/jquery.form-pickers.init.js"></script>

        <script type="text/javascript">
            jQuery(document).ready(function($) {
                $('.counter').counterUp({
                    delay: 100,
                    time: 1200
                });

                $(".knob").knob();

            });
        </script>
		
		<?php
		
		}
		
		?>
				
        <script src="/assets/js/jquery.core.js"></script>
        <script src="/assets/js/jquery.app.js"></script>

		<?php
		
			if (isset($javaScriptFooter) && !empty($javaScriptFooter))
			{
				echo '
				<script>
					'.$javaScriptFooter.'
				</script>';
			}
		?>

    </body>
</html>